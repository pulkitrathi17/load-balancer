package tech.arvindrachuri.lb.core;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ForwardingServlet extends HttpServlet {
    private static final List<Backend> backendSet = new ArrayList<>();

    public static void register(Backend backend) {
        backendSet.add(backend);
    }

    public static void unregister(Backend backend) {
        backendSet.remove(backend);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        Backend backend = backendSet.get(0);
        backendSet.remove(0);
        backendSet.add(backend);

        try {
            backend.forward(request, response);
        } catch (Exception e) {
            log.error("Unable to service the request {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
