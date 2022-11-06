import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

public class FileManager extends UnicastRemoteObject implements FileListInterface {

    private static HashMap<UUID, String> fileList = new HashMap<>();
    protected FileManager() throws RemoteException{

    }
    public FileManager(HashMap<UUID, String> fileList) throws RemoteException{
        FileManager.fileList =fileList;
    }
    public void base64ToFile(FileData f) throws IOException {
        byte[] decodedImg = Base64.getDecoder().decode(f.getFileBase64().getBytes(StandardCharsets.UTF_8));
        Path destinationFile = Paths.get("./savedFiles", f.getFileName());
        Files.write(destinationFile, decodedImg);
    }
    public UUID addFile(FileData f) throws RemoteException {
        UUID id = UUID.fromString(UUID.nameUUIDFromBytes(String.valueOf(f.getFileBase64()).getBytes()).toString());
        f.setFileID(id);
        fileList.put(f.getFileID(), f.getFileName());
        System.out.println(fileList);  //For testing
        try {
            base64ToFile(f);
        }catch (Exception e) {
            System.out.println("base64ToFile: " + e.getMessage()+"\n");
        }
        return id;
    }

    public HashMap<UUID, String> fileList() throws RemoteException {
        return fileList;
    }
}
