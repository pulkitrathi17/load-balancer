package tech.arvindrachuri.lb.routing;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import tech.arvindrachuri.lb.core.Backend;

@Slf4j
public class RoundRobinStrategy implements RoutingStrategy {

    private Long idx = -1L;
    private Map<byte[], Backend> ipMap = new HashMap<>();
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

    @Override
    public Backend getBackendForRequest(HttpServletRequest request, List<Backend> backends) {
        if (ipMap.containsKey(getIpAddress(request))) return ipMap.get(getIpAddress(request));
        if (idx > Integer.MAX_VALUE) idx = -1L;
        idx++;
        ipMap.put(getIpAddress(request), backends.get((idx.intValue()) % backends.size()));
        return backends.get((idx.intValue()) % backends.size());
    }

    private byte[] getIpAddress(HttpServletRequest request) {
        String ip = "";
        for (String header : POSSIBLE_IP_CONTAINING_HEADERS) {
            ip = request.getHeader(header);
            if (ip != null && !"unknown".equalsIgnoreCase(ip)) {
                break;
            }
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
            ip = request.getRemoteAddr();
        try {
            return InetAddress.getByName(ip).getAddress();
        } catch (UnknownHostException ex) {
            log.error("Unknown or Unparsable IP address [{}]", ip);
            return new byte[] {};
        }
    }
}
