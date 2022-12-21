import java.io.*;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProcessorManager extends UnicastRemoteObject implements ProcessorInterface, Serializable {
    RequestClass request;
    int port;
    String link;
    final static FileInterface FileInte;
    int threadCount;
    final static CoordenadorInterface coordenadorInterface;

    private AtomicBoolean finished = new AtomicBoolean();

    static {
        try {
            coordenadorInterface = (CoordenadorInterface) Naming.lookup("rmi://localhost:2050/coordenador");
            FileInte = (FileInterface) Naming.lookup("rmi://localhost:2021/storage");
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    protected ProcessorManager(String link) throws RemoteException {
        this.link = link;
    }

    public int getEstado() throws RemoteException {
        if (request == null)
            return 0;
        else
            return request.getEstado();
    }

    public static String getFile(String id) throws IOException {
        FileData f;
        f = FileInte.getFile(id);
        if (f == null) {
            return null;
        } else {
            return f.getFileName();
        }
    }

    public void exec(String fileID, String script) throws IOException {
        coordenadorInterface.addProcessosInacabados(link,fileID,script);
        threadCount++;
        String filename = getFile(fileID);
        finished.set(false);
        Thread t = (new Thread(() -> {
            try {
                String command = script + " " + filename;

                Process runtimeProcess  = Runtime.getRuntime().exec(command);
                System.out.println("Executing script: " + command);
                runtimeProcess.waitFor();

                System.out.println("Script executed successfully.");

                coordenadorInterface.removeProcessosInacabados(link);
                finished.set(true);
                threadCount--;
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
        t.start();
    }

    public boolean isFinished() throws RemoteException {
        return finished.get();
    }

    public ArrayList<String> outputFile(String fileID) throws IOException {
        try {
            String filename = getFile(fileID);
            ArrayList<String> outputLines = new ArrayList<>();
            String file = "Storage\\src\\savedFiles\\outfile_" + filename;
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                outputLines.add(line);
            }
            return outputLines;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendHeartbeat(String multicastMessage) throws RemoteException {
        Thread t = (new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket();
                InetAddress group = InetAddress.getByName("230.0.0.0");
                byte[] buffer = multicastMessage.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, 4446);
                socket.send(packet);
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        t.start();
    }

    final public Runnable processorInfo = () -> {
        //final HashMap<String, String> processsorInfo = new HashMap<>();
        final HashMap<String, Integer> processsorInfo = new HashMap<>();

        /*
        ThreadMXBean liveThreadCount = ManagementFactory.getThreadMXBean();
        OperatingSystemMXBean processCPULoad = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

        String liveThreadCountString = String.valueOf(liveThreadCount.getThreadCount());
        String processCPULoadDouble = String.valueOf(processCPULoad.getProcessCpuLoad());
        String heapMemoryUsageString = String.valueOf(heapMemoryUsage.getUsed());

        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        String GVMName = bean.getName();
        long PID = Long.parseLong(GVMName.split("@")[0]);

        String makeCompoundKey = port + "-" + liveThreadCountString + "->" + processCPULoadDouble;
        processsorInfo.put(makeCompoundKey, heapMemoryUsageString);
        */
        processsorInfo.put(link, threadCount);
        //System.out.println(processsorInfo);

        try {
            sendHeartbeat(processsorInfo.toString());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    };
}
