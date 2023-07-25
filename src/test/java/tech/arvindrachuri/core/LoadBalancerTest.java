package tech.arvindrachuri.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.arvindrachuri.lb.core.Backend;
import tech.arvindrachuri.lb.core.ForwardingServlet;
import tech.arvindrachuri.lb.core.LoadBalancer;

@Slf4j
public class LoadBalancerTest {

    private final LoadBalancer loadBalancer = new LoadBalancer();

    @Test
    public void LoadBalancerStartTest() throws Exception {
        try {
            Assertions.assertDoesNotThrow(loadBalancer::start);
        } finally {
            loadBalancer.stop();
        }
    }

    @Test
    public void LoadBalancerStopTest() throws Exception {
        try {
            loadBalancer.start();
        } catch (Exception ex) {
            log.error("Test couldn't start");
            throw ex;
        }
        Assertions.assertDoesNotThrow(loadBalancer::stop);
    }
}
