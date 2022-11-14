import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface ProcessorInterface extends Remote {
    int GetEstado() throws RemoteException;
    void Exec(String fileID, String url) throws RemoteException;
}