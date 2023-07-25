package tech.arvindrachuri.lb.routing;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import tech.arvindrachuri.lb.core.Backend;

public class RoundRobinStrategy implements RoutingStrategy {

    private Long idx = -1L;

    @Override
    public Backend getBackendForRequest(HttpServletRequest request, List<Backend> backends) {
        if (idx > Integer.MAX_VALUE) idx = -1L;
        idx++;
        return backends.get((idx.intValue()) % backends.size());
    }
}
