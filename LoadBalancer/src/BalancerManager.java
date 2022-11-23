import java.io.IOException;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;

public class BalancerManager extends UnicastRemoteObject implements BalancerInterface{

    final static ArrayList<String> activeProcessors = new ArrayList<>();

    final static HashMap<String,String> processorsDate = new HashMap<>();
    protected BalancerManager() throws RemoteException {
    }
    public ArrayList<String> SendRequest(String fileID) throws IOException {
        ProcessorInterface processorInterface;
        ArrayList<String> output = new ArrayList<>();
        try {
            processorInterface = (ProcessorInterface) Naming.lookup("rmi://localhost:2022/processor");
        }catch (NotBoundException | MalformedURLException a) {
            throw new RuntimeException(a);
        }
        processorInterface.exec(fileID);
        return output;
    }

    public void threadCreatorBalancer() throws RemoteException{
        byte[] buf = new byte[256];
        Thread t = (new Thread(() -> {
            try{
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
                    activeProcessorsDel(received);
                    if ("end".equals(received)) {
                        //Proximo Sprint
                        break;
                    }
                }
                socket.leaveGroup(group);
                socket.close();
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        }));
        t.start();
    }

    public void activeProcessorsAdd(String received){
        String receivedToSplit = received.substring(1);
        String[] arrofreceived = receivedToSplit.split("-",2);
        String PID = arrofreceived[0];
        if(!activeProcessors.contains(PID)){
            activeProcessors.add(PID);
        }
        System.out.println("Processadores ativos: ");
        System.out.println(activeProcessors);
    }

    public void activeProcessorsDel(String received) throws ParseException {
        String receivedToSplit = received.substring(1);
        String[] arrofreceived = receivedToSplit.split("-",2);
        String PID = arrofreceived[0];
        String date = String.valueOf(LocalTime.now());
        processorsDate.put(PID,date);
        System.out.println(processorsDate);

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        if(!processorsDate.isEmpty())
            for (Map.Entry<String, String> set : processorsDate.entrySet()) {
                String process = set.getKey();
                Date dateOfRequConverted = format.parse(processorsDate.get(process));
                Date datenowConverted = format.parse(date);
                long difference = datenowConverted.getTime() - dateOfRequConverted.getTime();
                if (difference > 20000) {
                    activeProcessors.remove(process);
                    processorsDate.remove(process);
                    System.out.println("O processador com PID " + process + " já não está ativo");
                }
            }
    }
}