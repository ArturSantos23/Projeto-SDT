import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;

public class Processor_Main implements Serializable {
    public static int port = 2024;
    public static Registry r = null;
    public static ProcessorManager processor;
    static String link;

    public static void main(String[] args) {
        String porta = port + "";
        String ip = "127.0.0.1";
        String rebindName = "processor";
        String serverID = UUID.nameUUIDFromBytes(porta.getBytes()).toString();
        new FileData(serverID, "1", porta);
        try {
            System.setProperty("java.rmi.server.hostname", ip);
            r = LocateRegistry.createRegistry(port);
        } catch (RemoteException a) {
            a.printStackTrace();
        }

        try {
            link = "rmi://" + ip + ":" + port + "/" + rebindName;
            processor = new ProcessorManager(link);
            r.rebind(rebindName, processor);


            RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
            String GVMName = bean.getName();
            long PID = Long.parseLong(GVMName.split("@")[0]);

            System.out.println("Processor ready\n" + "PID:" + PID);
            //System.out.println("Processor info: ");
            ScheduledExecutorService executor = newScheduledThreadPool(5);
            executor.scheduleAtFixedRate(processor.processorInfo, 0, 5, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("Processor main " + e.getMessage());
        }
    }
}
