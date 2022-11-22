import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

public class Processor_Main implements Serializable {
    public static Registry r = null;
    public static ProcessorManager processor;
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        String serverID = UUID.nameUUIDFromBytes("2022".getBytes()).toString();
        FileData p=new FileData(serverID,"1","2022");
        try {
            System.setProperty("java.rmi.server.hostname","127.0.0.1");
            r = LocateRegistry.createRegistry(2022);
        } catch (RemoteException a) {
            a.printStackTrace();
        }

        try {
            processor = new ProcessorManager();
            r.rebind("processor", processor);
            processor.Teikirize();
            System.out.println("Processor ready\n");
        } catch (Exception e) {
            System.out.println("Processor main " + e.getMessage());
        }
    }
}
