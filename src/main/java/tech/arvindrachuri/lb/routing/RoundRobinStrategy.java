package tech.arvindrachuri.lb.routing;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import tech.arvindrachuri.lb.core.Backend;

@Slf4j
public class RoundRobinStrategy implements RoutingStrategy {

    public RoundRobinStrategy() {
        log.debug("Reinitialized the strategy");
    }

    private Integer idx = -1;
    private final Map<Integer, Backend> ipMap = new HashMap<>();
    private final Map<Integer, Long> ttlMap = new HashMap<>();
    private static final String[] POSSIBLE_IP_CONTAINING_HEADERS = {
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR"
    };

    private void mapContents() {

        for (Map.Entry<Integer, Backend> ips : ipMap.entrySet()) {
            log.debug("IP Map Client Ip {} is mapped to Backend {}", ips.getKey(), ips.getValue());
        }
    }

    @Override
    public Backend getBackendForRequest(HttpServletRequest request, List<Backend> backends) {
        if (log.isDebugEnabled()) {
            mapContents();
        }
        Integer clientIp = getIpAddress(request);
        if (ipMap.containsKey(clientIp)) {
            if ((System.currentTimeMillis() - ttlMap.get(clientIp)) < 5000L) {
                ttlMap.put(clientIp, System.currentTimeMillis());
                return ipMap.get(clientIp);
            }
        }
        idx = (idx+1)% backends.size();
        Backend selectedBackend = backends.get(idx);
        ipMap.put(clientIp, selectedBackend);
        ttlMap.put(clientIp, System.currentTimeMillis());
        return selectedBackend;
    }

    private Integer getIpAddress(HttpServletRequest request) {
        String ip = "";
        for (String header : POSSIBLE_IP_CONTAINING_HEADERS) {
            ip = request.getHeader(header);
            if (ip != null && !"unknown".equalsIgnoreCase(ip)) {
                break;
            }
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
            ip = request.getRemoteAddr();
        log.debug("Client IP address resolved to [{}]", ip);
        try {
            log.debug("IP address as string {}", InetAddress.getByName(ip).getHostAddress());
            return getIntAddress(InetAddress.getByName(ip).getAddress());
        } catch (UnknownHostException ex) {
            log.error("Unknown or Unparsable IP address [{}]", ip);
            return 0x0;
        }
    }

    private Integer getIntAddress(byte[] address) {
        ByteBuffer intbuff = ByteBuffer.allocate(address.length);
        intbuff.put(address);
        intbuff.rewind();
        return intbuff.getInt();
    }
}
