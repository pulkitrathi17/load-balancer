package tech.arvindrachuri.lb.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import tech.arvindrachuri.lb.core.Backend;
import tech.arvindrachuri.lb.core.ForwardingServlet;
import tech.arvindrachuri.lb.core.LoadBalancer;
import tech.arvindrachuri.lb.routing.RoundRobinStrategy;
import tech.arvindrachuri.lb.routing.RoutingStrategy;

public class LoadBalancerModule extends AbstractModule {

    private final LoadBalancerConfig config;

    public LoadBalancerModule(LoadBalancerConfig config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        bind(LoadBalancerConfig.class).toInstance(this.config);
        bind(LoadBalancer.class).in(Singleton.class);
        bind(ForwardingServlet.class).in(Singleton.class);
    }

    @Provides
    public RoutingStrategy getRoutingStrategy() {
        return new RoundRobinStrategy();
    }

    @Provides
    public List<Backend> getBackends() {
        try {
            List<Backend> backends = new ArrayList<>();
            for (String backendIP : this.config.getBackendSet()) {
                backends.add(new Backend(backendIP));
            }
            return backends;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
