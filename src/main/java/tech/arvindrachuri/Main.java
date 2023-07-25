package tech.arvindrachuri;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import tech.arvindrachuri.lb.core.LoadBalancer;
import tech.arvindrachuri.lb.module.LoadBalancerModule;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new LoadBalancerModule());
        LoadBalancer loadBalancer = injector.getInstance(LoadBalancer.class);
        loadBalancer.start();

        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () -> {
                                    try {
                                        log.info("Running Shutdown hook");
                                        loadBalancer.stop();
                                    } catch (Exception ex) {
                                        log.error("Error occurred during cleanup. Exiting...");
                                    }
                                }));
    }
}
