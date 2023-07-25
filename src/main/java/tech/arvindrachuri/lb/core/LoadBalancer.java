package tech.arvindrachuri.lb.core;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

@Slf4j
public class LoadBalancer {

    private Server lbServer;

    public void start() throws Exception {
        log.info("Starting Load Balancer");
        lbServer = new Server();
        ServerConnector connector = new ServerConnector(lbServer);
        connector.setPort(8008);
        lbServer.setConnectors(new Connector[] {connector});

        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(ForwardingServlet.class, "/*");
        lbServer.setHandler(servletHandler);
        lbServer.start();
    }

    public void stop() throws Exception {
        for (Connector connector : lbServer.getConnectors()) {
            connector.stop();
        }
        lbServer.stop();
    }
}
