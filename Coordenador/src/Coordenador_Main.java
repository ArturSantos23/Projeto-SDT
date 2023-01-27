import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;

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
            ScheduledExecutorService executor = newScheduledThreadPool(5);
            executor.scheduleAtFixedRate(coordenadorManager.runnable, 0, 1, TimeUnit.SECONDS);

        } catch (Exception e) {
            System.out.println("Coordenador main " + e.getMessage());
        }
    }
}