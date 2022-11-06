import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

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
            FileListInterface l;
            File f = new File("C:\\Users\\artur\\Desktop\\Artur\\TesteV2.jpg");
            String base64 = FileToBase64(f);
            UUID UUID = null;

            String serverId = loadBalance.getServer(String.valueOf(i));
            String[] serverIdSplited = serverId.split("//",2);
            String[] serverName = serverIdSplited[1].split("/",2);
            try{
                l = (FileListInterface) Naming.lookup(serverId);
                FileData fd = new FileData(null, "TesteV2.jpg", base64);
                UUID = l.addFile(fd);

            } catch (ConnectException e) {
                System.out.println("O servidor ["+serverName[0]+"] não consegiu dar resposta.");
                return;
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Número máximo de respostas atingidas");
                return;
            } catch(RemoteException e) {
                System.out.println(e.getMessage());
            } catch(Exception e) {
                e.printStackTrace();
            }
            System.out.println("Resposta dada com sucesso pelo servidor: "+serverName[0]);
            System.out.print("Identificador do ficheiro: ");
            System.out.println(UUID+"\n");
        }
    }

    public static void GetServer(LoadBalancer loadBalance) {
        doGetServer(loadBalance, 12);
    }

    public static void loadBalance() {
        GetServer(new WeightRoundRobin());
    }

    public static void main(String[] args) {
        loadBalance();
    }
}
