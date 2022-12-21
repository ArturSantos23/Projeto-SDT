import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class Storage_Main implements Serializable {
    public static Registry r = null;

    public static FileManager fileList;

    private static final RaftNode node1 = new RaftNode();
    private static final RaftNode node2 = new RaftNode();
    private static final RaftNode node3 = new RaftNode();
    private static final List<RaftNode> nodes = new ArrayList<RaftNode>();
    static {
        nodes.add(node1);
        nodes.add(node2);
        nodes.add(node3);
    }

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            r = LocateRegistry.createRegistry(2021);
        } catch (RemoteException a) {
            a.printStackTrace();
        }

        try {
            fileList = new FileManager();
            r.rebind("storage", fileList);

            System.out.println("Storage server ready\n");
        } catch (Exception e) {
            System.out.println("Storage server main " + e.getMessage());
        }
    }
}
