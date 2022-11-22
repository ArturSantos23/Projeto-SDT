import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;

public interface ProcessorInterface extends Remote {
    int GetEstado() throws RemoteException;
    String Exec(String fileID) throws IOException;
    ArrayList<String> outputFile(String filename) throws IOException;
}