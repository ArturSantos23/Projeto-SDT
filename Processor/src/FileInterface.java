import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileInterface extends Remote {
    void base64ToFile(FileData f) throws IOException;

    String addFile(FileData f) throws RemoteException;

    FileData getFile(String UIDD) throws IOException;
}