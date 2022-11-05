import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server2 {
    public static Registry r = null;

    public static FileManager fileList;

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname","127.0.0.2");
            r = LocateRegistry.createRegistry(2022);
        } catch (RemoteException a) {
            a.printStackTrace();
        }

        try {
            fileList = new FileManager();
            r.rebind("filelist", fileList);

            System.out.println("File server ready");
        } catch (Exception e) {
            System.out.println("File server main " + e.getMessage());
        }
    }
}
