package tech.arvindrachuri.lb.core;

import com.google.inject.Inject;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

@Slf4j
public class LoadBalancer {

    private Server lbServer;

    private final ForwardingServlet servlet;

    @Setter Integer maxThreads;
    @Setter Integer minThreads;

    @Inject
    public LoadBalancer(ForwardingServlet servlet) {
        log.info("Setting servlet {}", servlet.getServletInfo());
        this.servlet = servlet;
    }

    public void start() throws Exception {
        log.info("Starting Load Balancer");
        setDefaultsIfNull();
        ThreadPool threadPool = new ExecutorThreadPool(maxThreads, minThreads);
        lbServer = new Server(threadPool);
        ServerConnector connector = new ServerConnector(lbServer);
        connector.setPort(8008);
        lbServer.setConnectors(new Connector[] {connector});

        ServletHandler servletHandler = new ServletHandler();
        log.info("Servlet is {}", servlet);
        ServletHolder servletHolder = new ServletHolder(servlet);

        servletHandler.addServletWithMapping(servletHolder, "/*");

        lbServer.setHandler(servletHandler);

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
