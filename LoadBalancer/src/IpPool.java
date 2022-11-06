import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IpPool {
    public static Map<String, Integer> ipMap = new ConcurrentHashMap<>();

    static {
        ipMap.put("rmi://127.0.0.1:2021/filelist", 4);
        ipMap.put("rmi://127.0.0.2:2022/filelist", 7);
        ipMap.put("rmi://127.0.0.3:2023/filelist", 5);
    }
}
