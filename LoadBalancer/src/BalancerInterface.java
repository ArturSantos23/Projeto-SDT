import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.util.UUID;

public interface BalancerInterface extends Remote {
    UUID SendRequest(RequestClass r) throws IOException, NotBoundException, InterruptedException;
}