import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

import static java.nio.file.Files.readAllBytes;

public class ProcessorManager extends UnicastRemoteObject implements ProcessorInterface, Serializable {
    RequestClass request;
    FileData f;
    static FileInterface FileInte;

    HashMap<String, String> output = new HashMap<>();

    static {
        try {
            FileInte = (FileInterface) Naming.lookup("rmi://localhost:2021/storage");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    protected ProcessorManager() throws RemoteException, MalformedURLException, NotBoundException {
    }
    public int GetEstado() throws RemoteException {
        if(request==null)
            return 0;
        else
            return request.getEstado();
    }
    /*public static String FileToBase64(File file){
        try {
            byte[] fileContent = readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException("could not read file " + file, e);
        }
    }
    public static void saveOutputFile() throws IOException{
        File f = new File("C:\\Users\\aguia\\Desktop\\savedFiles\\outfile.txt");
        String base64 = FileToBase64(f);
        FileData fd = new FileData(null, "outfile.txt", base64);
        String UUID = FileInte.addFile(fd);
        System.out.println("Ficheiro guardado!");
        System.out.println(UUID);
    }*/

    public void Exec(String fileID, String url) throws RemoteException
    {
        output.put(fileID, url);
        try {
            Process runtimeProcess = Runtime.getRuntime().exec(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(runtimeProcess.getInputStream()));
            StringBuilder output = new StringBuilder();

            String line;
            while((line = reader.readLine()) != null){
                output.append(line).append(System.getProperty("line.separator"));
            }
            runtimeProcess.waitFor();
            reader.close();

            System.out.println("Script executado!");
            System.out.println("Ficheiro guardado!");
            System.out.println(fileID);

            //saveOutputFile();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
