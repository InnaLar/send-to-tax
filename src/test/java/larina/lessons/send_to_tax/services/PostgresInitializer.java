package larina.lessons.send_to_tax.services;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresInitializer {
    public static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:14.5"); // укажите вашу версию

    static {
        POSTGRES_CONTAINER.start();
    }
}
