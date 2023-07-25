package tech.arvindrachuri.lb.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import tech.arvindrachuri.lb.core.Backend;
import tech.arvindrachuri.lb.core.BackendConfiguration;
import tech.arvindrachuri.lb.core.ForwardingServlet;
import tech.arvindrachuri.lb.core.LoadBalancer;
import tech.arvindrachuri.lb.routing.RoundRobinStrategy;
import tech.arvindrachuri.lb.routing.RoutingStrategy;

public class LoadBalancerModule extends AbstractModule {

    @Provides
    public LoadBalancer getLoadBalancer(ForwardingServlet servlet) {
        return new LoadBalancer(servlet);
    }

    @Provides
    public ForwardingServlet getForwardingServlet(BackendConfiguration configuration) {
        return new ForwardingServlet(configuration);
    }

    @Provides
    public BackendConfiguration getBackendConfiguration(
            RoutingStrategy routingStrategy, List<Backend> backendList) {
        return new BackendConfiguration(routingStrategy, backendList);
    }

    @Provides
    public RoutingStrategy getRoutingStrategy() {
        return new RoundRobinStrategy();
    }

    @Provides
    public List<Backend> getBackends() {
        try {
            return Arrays.asList(
                    new Backend("127.0.0.1:9000"),
                    new Backend("127.0.0.1:9001"),
                    new Backend("127.0.0.1:9002"));
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
