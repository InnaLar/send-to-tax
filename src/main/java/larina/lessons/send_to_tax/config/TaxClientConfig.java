package larina.lessons.send_to_tax.config;

import larina.lessons.send_to_tax.clients.TaxClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class TaxClientConfig {
    @Bean(name = "taxClient")
    public TaxClient taxClient() {
        RestClient client = RestClient.builder()
                .baseUrl("http://localhost:8100")
                .requestInterceptor((request, body, execution) -> {
                    log.info("REQ {} {} body={}", request.getMethod(), request.getURI(), new String(body));
                    var response = execution.execute(request, body);
                    log.info("RES status={}", response.getStatusCode());
                    return response;
                })
                .build();
        return new TaxClient(client);
    }
}
