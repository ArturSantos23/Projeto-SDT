import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class BalancerManager extends UnicastRemoteObject implements BalancerInterface {
    public ProcessorInterface processorInterface;
    public CoordenadorInterface coordenadorInterface;
    public static ConcurrentHashMap<String, String> activeProcessors = new ConcurrentHashMap<>();
    public String bestProcessor;
    public static ConcurrentHashMap<String,String> processingHistory = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, String> waitList = new ConcurrentHashMap<>();

    protected BalancerManager() throws RemoteException {
    }

    public ArrayList<String> SendRequest(String fileID, String script) throws IOException, InterruptedException {
        ProcessorInterface processorInterface;
        ArrayList<String> output = new ArrayList<>();
        try {
             processorInterface = (ProcessorInterface) Naming.lookup(bestProcessor);
        } catch (NotBoundException a) {
            throw new RuntimeException(a);
        }
        if(activeProcessors.get(bestProcessor).equals("0")){
            System.out.println("Added to processor");
            processorInterface.exec(fileID,script);
            while (!processorInterface.isFinished()) {
                TimeUnit.SECONDS.sleep(1);
            }
            processingHistory.put(bestProcessor,fileID+"+"+script);
            TimeUnit.SECONDS.sleep(1);
            output = processorInterface.outputFile(fileID);
        }
        else{
            for (Map.Entry<String, String> entry : activeProcessors.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if(Integer.parseInt(value) > 0){
                   waitList.put(fileID, script);
                   System.out.println("Added to waitList");
                   executeWaitList();
                }
                else{
                    try {
                        processorInterface = (ProcessorInterface) Naming.lookup(key);
                    } catch (NotBoundException a) {
                        throw new RuntimeException(a);
                    }
                    System.out.println("Added to processor");
                    processorInterface.exec(fileID,script);
                    while (!processorInterface.isFinished()) {
                        TimeUnit.SECONDS.sleep(1);
                    }
                    processingHistory.put(bestProcessor,fileID+"+"+script);
                    TimeUnit.SECONDS.sleep(1);
                    output = processorInterface.outputFile(fileID);
                }
            }
        }
        return output;
    }

    public void executeWaitList() throws IOException, InterruptedException {
        Thread t = (new Thread(() -> {
            if (waitList.size() > 0) {
                ArrayList<String> execution = null;
                while (true){
                    for (Map.Entry<String, String> entry : waitList.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        try {
                            execution = SendRequest(key, value);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        if(execution != null){
                            waitList.remove(key);
                            System.out.println("Removed from waitList");
                        }
                    }
                }
            }
        }));
    }

    public int getProcEstado() throws RemoteException {
        int state;
        ProcessorInterface processorInterface;
        try {
            processorInterface = (ProcessorInterface) Naming.lookup(bestProcessor);
        } catch (NotBoundException a) {
            throw new RuntimeException(a);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        state = processorInterface.getEstado();
        return state;
    }

    public void addProcessor(HashMap<String, String> h) throws RemoteException {
        activeProcessors.putAll(h);
    }

    public void saveBestProcessor(String bestProcessor) throws RemoteException {
        this.bestProcessor = bestProcessor;
    }

    public void executeInAnotherProcessor() throws IOException, InterruptedException {
        try {
            coordenadorInterface = (CoordenadorInterface) Naming.lookup("rmi://localhost:2050/coordenador");
        } catch (NotBoundException a) {
            throw new RuntimeException(a);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Executing in another processor");
        HashMap<String, String> lista = coordenadorInterface.getProcessosInacabados();
        System.out.println("Lista de processos inacabados: " + lista);
        if (!lista.isEmpty()) {
            System.out.println("Lista não está vazia");

            List keys = new ArrayList(lista.keySet());
            for (int i = 0; i < keys.size(); i++) {
                System.out.println("A executar o processo: " + keys.get(i));
                Object obj = keys.get(i);
                lista.get(obj);
                String[] parts = lista.get(obj).split("\\+");
                String fileID = parts[0];
                System.out.println("FileID: " + fileID);
                String script = parts[1];
                System.out.println("Script: " + script);
                SendRequest(fileID, script);
                lista.remove(obj);
                coordenadorInterface.removeProcessosInacabados(obj.toString());
            }
        }
    }
}