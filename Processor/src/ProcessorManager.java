import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProcessorManager extends UnicastRemoteObject implements ProcessorInterface, Serializable {
    RequestClass request;
    int port;
    String link;
    int activeProcessorSize;
    int consensusProcessors = 0;
    volatile private Instant lastHeartBeat = Instant.now();
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

    volatile boolean threadStatus = true;

    public synchronized void sendCoordenadorFailConsensus() throws IOException {
        Thread t = (new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket(4450);
                InetAddress group = InetAddress.getByName("240.0.0.0");
                String message = "fail";
                byte[] buf = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4448);
                socket.send(packet);
                socket.close();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                throw new RuntimeException(e);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
           t.start();
    }

    public synchronized void receiveCoordenadorConsensus() throws IOException {
        Thread t = (new Thread(() -> {
            try {
                MulticastSocket socket = new MulticastSocket(4450);
                InetAddress group = InetAddress.getByName("240.0.0.0");
                socket.joinGroup(group);
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(
                        packet.getData(), 0, packet.getLength());
                if (received.equals("fail")) {
                    consensusProcessors++;
                    declareConsensus();
                }
                socket.leaveGroup(group);
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        t.start();
    }

    public synchronized void declareConsensus() throws IOException {
        if (consensusProcessors > activeProcessorSize/2) {
            System.out.println("Coordenador Failed By Consensus");
        }
    }
    public synchronized void checkAliveCoordenador() throws IOException, NotBoundException {
        Thread t = (new Thread() {
            public void run() {
                    try {
                        MulticastSocket socket = new MulticastSocket(4447);
                        InetAddress group = InetAddress.getByName("231.0.0.0");
                        socket.joinGroup(group);
                        String received = null;
                        while (true) {
                            byte[] buf = new byte[256];
                            DatagramPacket packet = new DatagramPacket(buf, buf.length);
                            socket.receive(packet);
                            received = new String(
                                    packet.getData(), 0, packet.getLength());
                            if (received != null) {
                                activeProcessorSize = Integer.parseInt(received.substring(5));
                                threadStatus = true;
                                System.out.println("Received alive message from coordenador");
                                lastHeartBeat = Instant.now();
                                break;
                            }
                        }
                        socket.leaveGroup(group);
                        socket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
            }
        });
        if(threadStatus == true){
            t.start();
        }
        else{
            t.interrupt();
        }
    }

    public synchronized void handleCoordenadorFailure(){
        Thread t = (new Thread(){
            public void run(){
                while(threadStatus == true){
                    Instant current, interval;
                    current = Instant.now();
                    interval = Instant.ofEpochSecond(ChronoUnit.SECONDS.between(lastHeartBeat , current));
                    if(interval.getEpochSecond() > 30){
                        try {
                            sendCoordenadorFailConsensus();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Coordenador is down");
                        threadStatus = false;
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
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
                File path = new File("Storage\\src\\savedFiles\\outfile_" + filename);
                String base64 = FileToBase64(path);
                FileData f = new FileData(null, "outfile_" + filename, base64);
                String ID = FileInte.addFile(f);
                System.out.println("File ID: " + ID);

                coordenadorInterface.removeProcessosInacabados(link);
                finished.set(true);
                threadCount--;
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
        t.start();
    }

    public static String FileToBase64(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException("could not read file " + file, e);
        }
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
            checkAliveCoordenador();
            handleCoordenadorFailure();
            //receiveCoordenadorConsensus();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };
}
