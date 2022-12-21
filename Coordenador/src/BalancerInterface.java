import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public interface BalancerInterface extends Remote {
    public ArrayList<String> SendRequest(String fileID, String script) throws IOException, InterruptedException;

    void addProcessor(HashMap<String, String> h) throws RemoteException;

    void saveBestProcessor(String bestProcessor) throws RemoteException;

    int getProcEstado() throws RemoteException;

    ArrayList<String> executeInAnotherProcessor() throws IOException, InterruptedException;
}