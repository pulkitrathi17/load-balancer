package tech.arvindrachuri.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.arvindrachuri.lb.config.LoadBalancerConfig;
import tech.arvindrachuri.lb.core.BackendConfiguration;
import tech.arvindrachuri.lb.core.ForwardingServlet;
import tech.arvindrachuri.lb.core.LoadBalancer;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class LoadBalancerTest {
    @Mock private LoadBalancerConfig loadBalancerConfig;

    @Mock private ForwardingServlet forwardingServlet;

    @Mock private BackendConfiguration backendConfiguration;

    // if we  are manually creating object using mocked objects then we don't need @InjectMocks
    // annotation
    @InjectMocks private LoadBalancer loadBalancer;

    // we get a new instance of the loadBalancer object for each test as @InjectMocks creates new
    // object using mocks before running each test
    //    @BeforeEach
    //    public void setup() {
    //        loadBalancer = new LoadBalancer(forwardingServlet, loadBalancerConfig);
    //    }

    @Test
    public void loadBalancerStartSuccessTest() throws Exception {
        when(loadBalancerConfig.getPort()).thenReturn(8008);
        assertDoesNotThrow(() -> loadBalancer.start());
    }

    @Test
    public void loadBalancerStartFailureTest() throws Exception {
        when(loadBalancerConfig.getPort()).thenReturn(80085);
        loadBalancer.setMaxThreads(20);
        loadBalancer.setMinThreads(10);
        assertThrows(Exception.class, () -> loadBalancer.start());
    }
}
