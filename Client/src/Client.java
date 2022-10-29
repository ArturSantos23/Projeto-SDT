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
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException("could not read file " + file, e);
        }
    }
    public static void main(String[] args) {
        FileListInterface l;
        File f = new File("C:\\Users\\artur\\Desktop\\Artur\\Teste.png");
        String base64 = FileToBase64(f);
        String UUID;
        try{
            l = (FileListInterface) Naming.lookup("rmi://localhost:2022/filelist");
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
