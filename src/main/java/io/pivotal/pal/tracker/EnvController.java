package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EnvController {

    private String port;
    private String limit;
    private String index;
    private String address;

    public EnvController(@Value("${port:NOT SET}") String port, @Value("${memory.limit:NOT SET}") String limit,
                         @Value("${cf.instance.index:NOT SET}") String index, @Value("${cf.instance.addr:NOT SET}") String address) {
        this.port = port;
        this.limit = limit;
        this.index = index;
        this.address = address;
    }
    @GetMapping("/env")
    public Map<String, String> getEnv() {
        Map<String, String> result = new HashMap<>();
        result.put("PORT", port);
        result.put("MEMORY_LIMIT", limit);
        result.put("CF_INSTANCE_INDEX", index);
        result.put("CF_INSTANCE_ADDR", address);
        return result;
    }
}
