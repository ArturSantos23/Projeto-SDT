import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Base64;

public class Client_Main {
    public static String FileToBase64(File file){
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            //System.out.println("bytes: " + Arrays.toString(fileContent));
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException("could not read file " + file, e);
        }
    }

    public static void main(String[] args) {
        FileInterface l;
        File f = new File("C:\\Users\\artur\\Desktop\\Artur\\Teste.png");
        String base64 = FileToBase64(f);
        String UUID = null;
        //String[] serverName = null;

        try{
            //String[] serverIdSplited = serverId.split("//",2);
            //serverName = serverIdSplited[1].split("/",2);

            l = (FileInterface) Naming.lookup("rmi://localhost:2022/processor");
            FileData fd = new FileData(null, "Teste.png", base64);
            UUID = l.addFile(fd);

        } catch (ConnectException e) {
            //System.out.println("O servidor ["+serverName[0]+"] não consegiu dar resposta.");
            return;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Número máximo de respostas atingidas");
            return;
        } catch(RemoteException e) {
            System.out.println(e.getMessage());
        } catch(Exception e) {
            e.printStackTrace();
        }
        //System.out.println("Resposta dada com sucesso pelo servidor: "+serverName[0]);
        System.out.print("Identificador do ficheiro: ");
        System.out.println(UUID+"\n");
    }
}
