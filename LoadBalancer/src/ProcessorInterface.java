import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ProcessorInterface extends Remote {
    int getEstado() throws RemoteException;

    public void exec(String fileID, String script) throws IOException;

    ArrayList<String> outputFile(String filename) throws IOException;

    void sendHeartbeat(String multicastMessage) throws RemoteException;

    public boolean isFinished() throws RemoteException;
}