import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IpPool {
    public static Map<String, Integer> ipMap = new ConcurrentHashMap<>();

    static {
        ipMap.put("127.0.0.1", 4);
        ipMap.put("127.0.0.2", 3);
        ipMap.put("127.0.0.3", 3);
    }
}
