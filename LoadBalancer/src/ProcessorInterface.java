import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProcessorInterface extends Remote {
    void Send (RequestClass r) throws IOException, InterruptedException;

    int GetEstado() throws RemoteException;

    void Exec(String url) throws IOException;
}
