import com.sun.management.OperatingSystemMXBean;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class ProcessorManager extends UnicastRemoteObject implements ProcessorInterface, Serializable {
    RequestClass request;
    static FileInterface FileInte;

    ArrayList<String> output = new ArrayList<>();

    static {
        try {
            FileInte = (FileInterface) Naming.lookup("rmi://localhost:2021/storage");
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    protected ProcessorManager() throws RemoteException {
    }
    public int GetEstado() throws RemoteException {
        if(request==null)
            return 0;
        else
            return request.getEstado();
    }

    public static String getFile(String id) throws IOException{
        FileData f;
        f = FileInte.GetFile(id);
        if (f==null){
            return null;
        }
        else{
            return f.getFileName();
        }

    }
    public String Exec(String fileID) {
        String filename;
        output.add(fileID);
        try {
            filename = getFile(fileID);
            String path = "Teste.bat ";
            Runtime.getRuntime().exec(path + filename);

            System.out.println("Script executado!");
            System.out.println("Ficheiro guardado!");
            System.out.println(filename);
            return filename;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public ArrayList<String> outputFile(String filename) {
        try {
            ArrayList<String> outputLines = new ArrayList<>();
            String file = "Storage\\src\\savedFiles\\outfile_"+filename;
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                outputLines.add(line);
            }
            return outputLines;
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void threadCreatorBalancer(String multicastMessage){
        byte[] buf = new byte[256];
        Thread t = (new Thread(() -> {
            try{
                MulticastSocket socket = new MulticastSocket(4446);
                InetAddress group = InetAddress.getByName("230.0.0.0");
                socket.joinGroup(group);
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String received = new String(
                            packet.getData(), 0, packet.getLength());
                    if ("end".equals(received)) {//O QUE VOU FAZER COM O QUE RECEBER
                        break;
                    }
                }
                socket.leaveGroup(group);
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        t.start();
    }

    public Runnable processorInfo = () -> {
        final HashMap<String, String> processsorInfo= new HashMap<>();

        ThreadMXBean liveThreadCount = ManagementFactory.getThreadMXBean();
        OperatingSystemMXBean processCPULoad = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

        String liveThreadCountString = String.valueOf(liveThreadCount.getThreadCount());
        String processCPULoadDouble = String.valueOf(processCPULoad.getProcessCpuLoad());
        String heapMemoryUsageString = String.valueOf(heapMemoryUsage.getUsed());

        String makeCompoundKey = liveThreadCountString+"->" + processCPULoadDouble;
        processsorInfo.put(makeCompoundKey, heapMemoryUsageString);

        System.out.println(processsorInfo);
    };
}
