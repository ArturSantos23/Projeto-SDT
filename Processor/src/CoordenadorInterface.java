import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface CoordenadorInterface extends Remote {
    void treatHeartBeat() throws RemoteException;

    HashMap<String, String> processosInacabados = new HashMap<>();
}
