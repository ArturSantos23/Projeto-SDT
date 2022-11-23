import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface BalancerInterface extends Remote {
    ArrayList<String> SendRequest(String fileID) throws IOException, NotBoundException, InterruptedException;
    void threadCreatorBalancer() throws RemoteException;
}