package tech.arvindrachuri.lb.core;

import com.google.inject.Inject;
import java.util.Random;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import tech.arvindrachuri.lb.config.LoadBalancerConfig;

@Slf4j
public class LoadBalancer {

    private Server lbServer;
    private Integer randomNum;

    private final ForwardingServlet servlet;

    @Setter Integer maxThreads;
    @Setter Integer minThreads;

    private final LoadBalancerConfig config;

    @Inject
    public LoadBalancer(ForwardingServlet servlet, LoadBalancerConfig config) {
        this.servlet = servlet;
        this.config = config;
        randomNum = new Random().nextInt();
    }

    public void start() throws Exception {
        // will print same number as we are using Singleton LoadBalancer instance
        log.info("Starting Load Balancer: {}", randomNum);
        setDefaultsIfNull();
        ThreadPool threadPool = new ExecutorThreadPool(maxThreads, minThreads);
        lbServer = new Server(threadPool);
        ServerConnector connector = new ServerConnector(lbServer);
        log.info("Server listening on port {}", config.getPort());
        connector.setPort(config.getPort());
        lbServer.setConnectors(new Connector[] {connector});

        ServletHandler servletHandler = new ServletHandler();
        log.info("Servlet is {}", servlet);
        ServletHolder servletHolder = new ServletHolder(servlet);

        servletHandler.addServletWithMapping(servletHolder, "/*");

        lbServer.setHandler(servletHandler);

        log.info("Using backend sets {}", config.getBackendSet());

        lbServer.start();
    }

    public void stop() throws Exception {
        for (Connector connector : lbServer.getConnectors()) {
            connector.stop();
        }
        lbServer.stop();
    }

    private void setDefaultsIfNull() {
        if (maxThreads == null) maxThreads = 100;
        if (minThreads == null) minThreads = 10;
    }
}
