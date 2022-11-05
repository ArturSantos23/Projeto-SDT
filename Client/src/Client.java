import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Base64;

public class Client {
    public static String FileToBase64(File file){
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            //System.out.println("bytes: " + Arrays.toString(fileContent));
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException("could not read file " + file, e);
        }
    }

    private static void doGetServer(LoadBalancer loadBalance, int queryTimes) {
        for (int i = 1; i <= queryTimes; i++) {
            String serverId = loadBalance.getServer(String.valueOf(i));
            System.out.printf("[%s] index:%s,%s%n", loadBalance.getClass().getSimpleName(), i, serverId);
        }
    }

    public static void doGetServer(LoadBalancer loadBalance) {
        doGetServer(loadBalance, 10);
    }

    public static void loadBalance() {
        //doGetServer(new IpHash());
        doGetServer(new WeightRoundRobin());
    }


    public static void main(String[] args) {
        loadBalance();

        ////////////////////////////////////////////////////////////////////////////////////////////

        FileListInterface l;
        File f = new File("C:\\Users\\artur\\Desktop\\Artur\\Teste.png");
        String base64 = FileToBase64(f);
        String UUID;
        try{
            l = (FileListInterface) Naming.lookup("rmi://127.0.0.1:2021/filelist");
            FileData fd = new FileData(null, "Teste.png", base64);
            l.addFile(fd);
            UUID = l.getFileID("Teste.png");
            System.out.print("Identificador do ficheiro: ");
            System.out.println(UUID);

        } catch(RemoteException e) {
            System.out.println(e.getMessage());
        }catch(Exception e) {e.printStackTrace();}
    }
}

/*Thread t = (new Thread() {
            public void run() {
                RMIRegistry.main(new String[0]);
                RMIReplicaManager.main(new String[0]);
                RMIServer1.main(new String[]{"2021"});
                RMIServer2.main(new String[]{"2022"});
                RMIServer3.main(new String[]{"2023"});
            }
        });
        t.start();
        Thread.sleep(1000); // garante que todos os serviços estão disponíveis antes de executar o código do cliente
 */