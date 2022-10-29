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

public class FileManager extends UnicastRemoteObject implements FileListInterface {

    private ArrayList<FileData> fileList = new ArrayList<FileData>();
    protected FileManager() throws RemoteException{

    }
    public FileManager(ArrayList<FileData> fileList) throws RemoteException{
        this.fileList=fileList;
    }
    public void base64ToFile(FileData f) throws IOException {
        byte[] decodedImg = Base64.getDecoder().decode(f.getFileBase64().getBytes(StandardCharsets.UTF_8));
        Path destinationFile = Paths.get("./savedFiles", f.getFileName());
        Files.write(destinationFile, decodedImg);
    }
    public void addFile(FileData f) throws RemoteException {
        String fileName = f.getFileName();
        String resultUUID = UUID.nameUUIDFromBytes(fileName.getBytes()).toString();

        //System.out.println(f.getFileName());  //For testing
        //System.out.println(resultUUID);   //For testing

        UUID id = UUID.fromString(resultUUID);;
        f.setFileID(id);
        this.fileList.add(f);
        try {
            base64ToFile(f);
        }catch (Exception e) {
            System.out.println("base64ToFile: " + e.getMessage());
        }
    }
    public String getFileID(String fileName) throws RemoteException{
        for(int i = 0; i < this.fileList.size(); ++i) {
            if (fileName.equals((this.fileList.get(i)).getFileName())) {
                return this.fileList.get(i).getFileID().toString();
            }
        }
        return null;
    }
    public ArrayList<FileData> fileList() throws RemoteException {
        return fileList;
    }
}
