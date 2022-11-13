import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileInterface extends Remote{
    void base64ToFile(FileData f) throws IOException;
    String addFile(FileData f) throws RemoteException;
    FileData GetFile(String UIDD) throws IOException;
    void FileOutput(String RequestID, FileData f) throws RemoteException;
}