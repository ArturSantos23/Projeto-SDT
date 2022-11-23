import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ProcessorInterface extends Remote {
    int getEstado() throws RemoteException;
    String exec(String fileID) throws IOException;
    ArrayList<String> outputFile(String filename) throws IOException;
    void threadCreatorProcessor(String multicastMessage) throws RemoteException;
}