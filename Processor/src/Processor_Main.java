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
    public static Registry r = null;
    public static ProcessorManager processor;
    public static void main(String[] args) {
        String serverID = UUID.nameUUIDFromBytes("2022".getBytes()).toString();
        new FileData(serverID, "1", "2022");
        try {
            System.setProperty("java.rmi.server.hostname","127.0.0.1");
            r = LocateRegistry.createRegistry(2022);
        } catch (RemoteException a) {
            a.printStackTrace();
        }

        try {
            processor = new ProcessorManager();
            r.rebind("processor", processor);

            RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
            String GVMName = bean.getName();
            long PID = Long.parseLong(GVMName.split("@")[0]);

            System.out.println("Processor ready\n"+"PID:"+PID);
            System.out.println("Processor info: ");
            ScheduledExecutorService executor = newScheduledThreadPool(5);
            executor.scheduleAtFixedRate(processor.processorInfo, 0, 3, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("Processor main " + e.getMessage());
        }
    }
}
