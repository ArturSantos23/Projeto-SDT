import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class BalancerManager extends UnicastRemoteObject implements BalancerInterface{

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
                    System.out.println(received);
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
}