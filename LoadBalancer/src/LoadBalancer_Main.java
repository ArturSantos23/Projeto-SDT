import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LoadBalancer_Main {

    public static Registry r = null;
    public static BalancerManager fileList;

    public static void main(String[] args) {
        try {
            r = LocateRegistry.createRegistry(2025);
        } catch (RemoteException a) {
            a.printStackTrace();
        }

        try {
            fileList = new BalancerManager();
            r.rebind("balancer", fileList);
            System.out.println("LoadBalancer ready\n");


        } catch (Exception e) {
            System.out.println("LoadBalancer main " + e.getMessage());
        }
    }
}
