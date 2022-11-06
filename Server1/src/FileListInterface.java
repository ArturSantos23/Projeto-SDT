import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.UUID;

public interface FileListInterface extends Remote{
    UUID addFile(FileData f) throws RemoteException;

    HashMap<UUID, String> fileList() throws RemoteException;
}