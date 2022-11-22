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
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

import static java.nio.file.Files.readAllBytes;

public class ProcessorManager extends UnicastRemoteObject implements ProcessorInterface, Serializable {
    RequestClass request;
    FileData f;
    static FileInterface FileInte;

    ArrayList<String> output = new ArrayList<>();

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

    public static String getFile(String id) throws IOException{
        String ID = id;
        System.out.println("ID:");
        FileData f = FileInte.GetFile(ID);
        if (f==null){
            System.out.println(f);
            return null;
        }
        else{
            return f.getFileName();
        }

    }

    public String Exec(String fileID) throws IOException {
        String filename = null;
        output.add(fileID);
        try {
            filename = getFile(fileID);
            String path = "C:\\Users\\aguia\\Desktop\\EI\\3A1S\\SDT\\Projeto-SDT\\Teste.bat ";
            Process runtimeProcess = Runtime.getRuntime().exec(path + filename);

            System.out.println("Script executado!");
            System.out.println("Ficheiro guardado!");
            System.out.println(filename);
            return filename;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public ArrayList<String> outputFile(String filename) throws IOException{
        try {
            ArrayList<String> outputLines = new ArrayList<String>();
            String file = "C:\\Users\\aguia\\Desktop\\EI\\3A1S\\SDT\\Projeto-SDT\\Storage\\src\\savedFiles\\outfile_"+filename;
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

}
