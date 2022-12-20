import java.io.IOException;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CoordenadorManager extends UnicastRemoteObject implements CoordenadorInterface {
    final static HashMap<String, String> activeProcessors = new HashMap<>();
    final static HashMap<String, String> activeProcessorsToSend = new HashMap<>();

    public static HashMap<String, String> processosInacabados = new HashMap<>();
    public BalancerInterface balancerInterface;

    protected CoordenadorManager(String balancerHostName, int balancerPort) throws RemoteException, NotBoundException {
        Registry r = LocateRegistry.getRegistry(balancerHostName, balancerPort);
        balancerInterface = (BalancerInterface) r.lookup("balancer");
    }

    public void treatHeartBeat() throws RemoteException {
        byte[] buf = new byte[256];
        Thread t = (new Thread(() -> {
            try {
                MulticastSocket socket = new MulticastSocket(4446);
                InetAddress group = InetAddress.getByName("230.0.0.0");
                socket.joinGroup(group);
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String received = new String(
                            packet.getData(), 0, packet.getLength());
                    //System.out.println(received);
                    activeProcessorsAdd(received);

                    if ("end".equals(received)) {
                        //Proximo Sprint
                        break;
                    }
                }
                socket.leaveGroup(group);
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        t.start();
    }


    public void activeProcessorsAdd(String received) throws RemoteException {
        String receivedToSplit = received.substring(1);
        String[] arrofreceived = receivedToSplit.split("=", 2);
        String port = arrofreceived[0];
        String threadsToSplit = arrofreceived[1];
        String[] arrofthreads = threadsToSplit.split("}", 2);
        String threads = arrofthreads[0];

        synchronized (activeProcessorsToSend) {
            activeProcessorsToSend.put(port, threads);
        }

        String date = String.valueOf(LocalTime.now());
        synchronized (activeProcessors) {
            activeProcessors.put(port, date);
        }
        balancerInterface.addProcessor(activeProcessorsToSend);

        String bestProcessor = bestProcessor();

        balancerInterface.saveBestProcessor(bestProcessor);

       // System.out.println("Processadores ativos: ");
        //System.out.println(activeProcessors);
    }

    public void delProcessor() throws RemoteException, RuntimeException, InterruptedException {
        Thread t = (new Thread() {
            public void run() {
                while (true) {
                    String date = String.valueOf(LocalTime.now());
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    Date datenowConverted = null;

                    try {
                        datenowConverted = format.parse(date);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    synchronized (activeProcessors) {
                        if (!activeProcessors.isEmpty()) {
                            List keys = new ArrayList(activeProcessors.keySet());
                            for (int i = 0; i < keys.size(); i++) {
                                Object obj = keys.get(i);
                                String ultimadataRequest = activeProcessors.get(obj);

                                Date dateOfRequConverted = null;
                                try {
                                    dateOfRequConverted = format.parse(ultimadataRequest);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                long difference = datenowConverted.getTime() - dateOfRequConverted.getTime();
                                if (difference > 30000) {
                                    activeProcessors.remove(obj);
                                    activeProcessorsToSend.remove(obj);
                                    String bestProcessor = bestProcessor();

                                    try {
                                        balancerInterface.saveBestProcessor(bestProcessor);
                                    } catch (RemoteException e) {
                                        throw new RuntimeException(e);
                                    }
                                    System.out.println("O processador " + obj + " já não está ativo");

                                    try {
                                        balancerInterface.addProcessor(activeProcessors);
                                        System.out.println("Verificar se Executar em outro processador");
                                        System.out.println(processosInacabados);
                                        balancerInterface.executeInAnotherProcessor();

                                    } catch (RemoteException e) {
                                        throw new RuntimeException(e);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        t.start();
    }

    public String bestProcessor() {
        int low = Integer.MAX_VALUE;
        String port = null;
        if (!activeProcessors.isEmpty()) {
            List keys = new ArrayList(activeProcessorsToSend.keySet());
            for (int i = 0; i < keys.size(); i++) {
                Object obj = keys.get(i);
                int value = Integer.parseInt(activeProcessorsToSend.get(obj));
                if (value < low) {
                    low = value;
                    port = obj.toString();
                }
            }
        }
        return port;
    }

    public void addProcessosInacabados(String link, String fileID, String script) throws RemoteException {
        processosInacabados.put(link, fileID + " + " + script);
        System.out.println("Processos Inacabados: ");
        System.out.println(processosInacabados);
    }
    public void removeProcessosInacabados(String link) throws RemoteException {
        processosInacabados.remove(link);
        System.out.println("Processos Inacabados: ");
        System.out.println(processosInacabados);
    }
    public HashMap<String,String> getProcessosInacabados() throws RemoteException {
        System.out.println("Processos Inacabados get: ");
        System.out.println(processosInacabados);
        return processosInacabados;
    }
}
