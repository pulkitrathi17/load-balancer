package tech.arvindrachuri.lb.config;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoadBalancerConfig {
    @NonNull private Integer port;

    @NonNull private List<String> backendSet;
}
