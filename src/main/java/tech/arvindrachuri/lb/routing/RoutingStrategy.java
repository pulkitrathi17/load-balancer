package tech.arvindrachuri.lb.routing;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import tech.arvindrachuri.lb.core.Backend;

public interface RoutingStrategy {
    Backend getBackendForRequest(HttpServletRequest request, List<Backend> backends);
}
