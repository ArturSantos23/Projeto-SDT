import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Processor_Main {
    public static Registry r = null;

    public static ProcessorManager processor;
    public static void main(String[] args) {
        //UUID serverID = UUID.fromString(UUID.nameUUIDFromBytes(String.valueOf("2021").getBytes()).toString());
        //ProcessorClass p=new ProcessorClass(identificador,1,2024);
        try {
            System.setProperty("java.rmi.server.hostname","127.0.0.1");
            r = LocateRegistry.createRegistry(2022);
        } catch (RemoteException a) {
            a.printStackTrace();
        }

        try {
            processor = new ProcessorManager();
            r.rebind("processor", processor);

            System.out.println("Processor ready\n");
        } catch (Exception e) {
            System.out.println("Processor main " + e.getMessage());
        }
    }
}
