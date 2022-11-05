import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;

public interface FileListInterface extends Remote{

    UUID addFile(FileData f) throws RemoteException;

    String getFileID(String fileName) throws RemoteException;

    ArrayList<FileData> fileList() throws RemoteException;
}