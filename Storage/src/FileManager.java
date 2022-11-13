import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

public class FileManager extends UnicastRemoteObject implements FileInterface {

    private static final ArrayList<FileData> fileList = new ArrayList<>();
    protected FileManager() throws RemoteException{
    }

    public void base64ToFile(FileData f) throws IOException {
        byte[] decodedImg = Base64.getDecoder().decode(f.getFileBase64().getBytes(StandardCharsets.UTF_8));
        Path destinationFile = Paths.get("C:\\Users\\artur\\IdeaProjects\\Projeto-SDT\\savedFiles", f.getFileName());
        Files.write(destinationFile, decodedImg);
    }

    public String addFile(FileData f) throws RemoteException {
        UUID id = UUID.fromString(UUID.nameUUIDFromBytes(String.valueOf(f.getFileBase64()).getBytes()).toString());
        f.setFileID(id.toString());
        fileList.add(f);
        System.out.println(fileList);  //For testing
        try {
            base64ToFile(f);
        }catch (Exception e) {
            System.out.println("base64ToFile Error: " + e.getMessage()+"\n");
        }
        return id.toString();
    }

    public FileData GetFile(String UIDD) throws IOException {
        for(int i=0; i < fileList.size(); i++)
        {
            if(UIDD.equals(fileList.get(i).getFileID()))
            {
                return fileList.get(i);
            }
        }
        return null;
    }
}
