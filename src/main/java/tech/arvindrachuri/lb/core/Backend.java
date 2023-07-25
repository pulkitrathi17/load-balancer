package tech.arvindrachuri.lb.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;

@Slf4j
public class Backend {
    private final InetSocketAddress ipAddr;
    private final HttpClient forwarder = new HttpClient();

    public Backend(String hostAddr) throws UnknownHostException {
        String[] parts = hostAddr.split(":");
        if (parts.length != 2)
            throw new UnknownHostException("The expected address is of the format hostIP:port");
        try {
            ipAddr =
                    new InetSocketAddress(
                            InetAddress.getByName(parts[0]), Integer.parseInt(parts[1]));
        } catch (NumberFormatException ex) {
            throw new UnknownHostException(ex.getMessage());
        }
    }

    public void forward(HttpServletRequest request, HttpServletResponse response) throws Exception {
        forwarder.start();
        String path = request.getRequestURI();
        String uri = "http://" + ipAddr.getHostString() + ":" + ipAddr.getPort() + path;
        log.info("Setting Request Uri to [{}]", uri);
        Request fwdRequest = forwarder.newRequest(uri);
        fwdRequest.headers(
                headers ->
                        request.getHeaderNames()
                                .asIterator()
                                .forEachRemaining(
                                        headerName ->
                                                headers.put(
                                                        headerName,
                                                        request.getHeader(headerName))));
        ContentResponse fwdResponse = fwdRequest.send();
        response.setStatus(fwdResponse.getStatus());
        fwdResponse
                .getHeaders()
                .asImmutable()
                .getFieldNames()
                .asIterator()
                .forEachRemaining(
                        headerName ->
                                response.setHeader(
                                        headerName, fwdResponse.getHeaders().get(headerName)));
        response.getOutputStream().write(fwdResponse.getContent());
        forwarder.stop();
    }
}
