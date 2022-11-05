import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;

public interface FileListInterface extends Remote{

    public UUID addFile(FileData f) throws RemoteException;

    public String getFileID(String fileName) throws RemoteException;

    public ArrayList<FileData> fileList() throws RemoteException;
}