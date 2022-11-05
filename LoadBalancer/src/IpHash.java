import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IpHash implements LoadBalancer {

    @Override
    public String getServer(String clientIp) {
        if (clientIp == null) {
            clientIp = "127.0.0.10";
        }
        Set<String> servers = IpPool.ipMap.keySet();
        List<String> serverList = new ArrayList<>(servers);
        String remoteId = clientIp;
        int index = remoteId.hashCode() % serverList.size();
        return serverList.get(index);
    }
}
