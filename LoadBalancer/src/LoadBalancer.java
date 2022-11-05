public interface LoadBalancer {
    String getServer(String clientIp);
}
