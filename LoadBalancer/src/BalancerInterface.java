import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public interface BalancerInterface extends Remote {
    public ArrayList<String> SendRequest(String fileID, String url) throws IOException, NotBoundException, InterruptedException;
}