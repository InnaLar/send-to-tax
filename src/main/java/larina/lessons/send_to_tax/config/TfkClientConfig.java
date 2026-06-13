package larina.lessons.send_to_tax.config;

import larina.lessons.send_to_tax.clients.TfkClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class TfkClientConfig {
    @Bean(name = "tfkClient")
    public TfkClient tfkClient() {
        RestClient client = RestClient.builder()
                .baseUrl("${tfk.base-url}")
                .requestInterceptor(((request, body, execution) -> {
                    log.info("REQ {} {} body={}", request.getMethod(), request.getURI(), new String(body));
                    var response = execution.execute(request, body);
                    log.info("RES status {}", response.getStatusCode());
                    return response;
                }))
                .build();
        return new TfkClient(client);
    }


}
