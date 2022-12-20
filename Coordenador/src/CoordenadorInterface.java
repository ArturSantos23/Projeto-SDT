import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface CoordenadorInterface extends Remote {
    void treatHeartBeat() throws RemoteException;
    public void addProcessosInacabados(String link, String fileID, String script) throws RemoteException;
    public void removeProcessosInacabados(String link) throws RemoteException;
    public HashMap<String,String> getProcessosInacabados() throws RemoteException;
}
