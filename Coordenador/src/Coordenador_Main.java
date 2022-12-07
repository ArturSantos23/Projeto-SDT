import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Coordenador_Main {
    public static Registry r = null;
    public static CoordenadorManager coordenadorManager;

    public static void main(String[] args) {
        try {
            r = LocateRegistry.createRegistry(2050);
        } catch (RemoteException a) {
            a.printStackTrace();
        }

        try {
            coordenadorManager = new CoordenadorManager("localhost", 2025);
            r.rebind("coordenador", coordenadorManager);
            System.out.println("Coordenador ready\n");
            coordenadorManager.threadCreatorBalancer();
            coordenadorManager.delProcessor();

            //balancerInterface.add(coiso)
        } catch (Exception e) {
            System.out.println("Coordenador main " + e.getMessage());
        }
    }
}