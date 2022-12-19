import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BalancerManager extends UnicastRemoteObject implements BalancerInterface {
    public ProcessorInterface processorInterface;
    public CoordenadorInterface coordenadorInterface;
    public static HashMap<String, String> activeProcessors = new HashMap<>();
    public String bestProcessor;

    protected BalancerManager() throws RemoteException {
    }

    public ArrayList<String> SendRequest(String fileID, String fileName, String script) throws IOException, InterruptedException {
        ProcessorInterface processorInterface;
        ArrayList<String> output = new ArrayList<>();
        try {
            Registry r = LocateRegistry.getRegistry("localhost", Integer.parseInt(bestProcessor));
            processorInterface = (ProcessorInterface) r.lookup("processor");
        } catch (NotBoundException a) {
            throw new RuntimeException(a);
        }
        processorInterface.exec(fileID,script);
        //wait for the file to be created

        //TimeUnit.SECONDS.sleep(10);
        output = processorInterface.outputFile(fileName);
        return output;
    }

    public int getProcEstado() throws RemoteException {
        int state;
        ProcessorInterface processorInterface;
        try {
            Registry r = LocateRegistry.getRegistry("localhost", Integer.parseInt(bestProcessor));
            processorInterface = (ProcessorInterface) r.lookup("processor");
        } catch (NotBoundException a) {
            throw new RuntimeException(a);
        }
        state = processorInterface.getEstado();
        return state;
    }

    public void addProcessor(HashMap<String, String> h) throws RemoteException {
        activeProcessors.putAll(h);
        System.out.println(h);
    }

    public void saveBestProcessor(String bestProcessor) throws RemoteException {
        this.bestProcessor = bestProcessor;
    }

    public ArrayList<String> executeInAnotherProcessor() throws IOException, InterruptedException {
        HashMap<String, String> lista = coordenadorInterface.processosInacabados;
        if (!lista.isEmpty()) {
            ProcessorInterface processorInterface;
            ArrayList<String> output = new ArrayList<>();
            try {
                Registry r = LocateRegistry.getRegistry("localhost", Integer.parseInt(bestProcessor));
                processorInterface = (ProcessorInterface) r.lookup("processor");
            } catch (NotBoundException a) {
                throw new RuntimeException(a);
            }
            List keys = new ArrayList(lista.keySet());
            for (int i = 0; i < keys.size(); i++) {
                Object obj = keys.get(i);
                lista.get(obj);
                String[] parts = lista.get(obj).split("\\+");
                String fileID = parts[0];
                String script = parts[1];
                processorInterface.exec(fileID, script);
            }

            TimeUnit.SECONDS.sleep(1);
            return output;
        }
        return null;
    }
}