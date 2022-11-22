import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class BalancerManager extends UnicastRemoteObject implements BalancerInterface{
    protected BalancerManager() throws RemoteException {
    }
    public ArrayList<String> SendRequest(String fileID) throws IOException {
        ProcessorInterface processorInterface = null;
        ArrayList<String> output = new ArrayList<>();
        try {
            processorInterface = (ProcessorInterface) Naming.lookup("rmi://localhost:2022/processor");
        }catch (NotBoundException | MalformedURLException a) {
            throw new RuntimeException(a);
        }
        processorInterface.Exec(fileID);
        return output;
    }
}