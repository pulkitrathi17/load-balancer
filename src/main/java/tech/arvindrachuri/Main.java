package tech.arvindrachuri;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import tech.arvindrachuri.lb.config.LoadBalancerConfig;
import tech.arvindrachuri.lb.config.LoadBalancerModule;
import tech.arvindrachuri.lb.core.LoadBalancer;

@Slf4j
public class Main {

    public void run(LoadBalancerConfig config) throws Exception {
        Injector injector = Guice.createInjector(new LoadBalancerModule(config));
        LoadBalancer loadBalancer1 = injector.getInstance(LoadBalancer.class);
        loadBalancer1.start();

        LoadBalancer loadBalancer2 = injector.getInstance(LoadBalancer.class);
        loadBalancer2.start();

        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () -> {
                                    try {
                                        log.info("Running Shutdown hook");
                                        loadBalancer1.stop();
                                        loadBalancer2.stop();
                                    } catch (Exception ex) {
                                        log.error("Error occurred during cleanup. Exiting...");
                                    }
                                }));
    }

    public LoadBalancerConfig parseConfig(String filePath) throws IOException {
        Yaml conf = new Yaml(new Constructor(LoadBalancerConfig.class, new LoaderOptions()));
        try (InputStream confStream = new FileInputStream(filePath)) {
            return conf.load(confStream);
        }
    }

    public static void main(String[] args) throws Exception {
        Main app = new Main();
        LoadBalancerConfig config = app.parseConfig(Paths.get("").toAbsolutePath() + "/lb.conf");
        app.run(config);
    }
}
