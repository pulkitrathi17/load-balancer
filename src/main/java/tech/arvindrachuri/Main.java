package tech.arvindrachuri;

import lombok.extern.slf4j.Slf4j;
import tech.arvindrachuri.lb.core.Backend;
import tech.arvindrachuri.lb.core.ForwardingServlet;
import tech.arvindrachuri.lb.core.LoadBalancer;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {
        final LoadBalancer loadBalancer = new LoadBalancer();
        ForwardingServlet.register(new Backend("127.0.0.1:9000"));
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
