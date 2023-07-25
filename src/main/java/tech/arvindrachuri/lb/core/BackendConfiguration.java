package tech.arvindrachuri.lb.core;

import com.google.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tech.arvindrachuri.lb.routing.RoutingStrategy;

@Slf4j
public class BackendConfiguration {

    @Getter private final RoutingStrategy routingStrategy;

    @Getter final List<Backend> backends;

    @Inject
    public BackendConfiguration(RoutingStrategy routingStrategy, List<Backend> backends) {
        this.routingStrategy = routingStrategy;
        this.backends = backends;
    }

    public Backend getBackend(HttpServletRequest request) {
        return routingStrategy.getBackendForRequest(request, backends);
    }
}
