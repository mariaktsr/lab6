package server.config;

//Конфигурация сервера

public class ServerConfig {

    private static final String ENV_FILE_NAME = "HUMAN_BEING_FILE";
    private static final String ENV_PORT = "SERVER_PORT";
    private static final int DEFAULT_PORT = 12345;

    private final String fileName;
    private final int port;

    public ServerConfig() {
        //Чтение имени файла
        this.fileName = System.getenv(ENV_FILE_NAME);
        if (this.fileName == null || this.fileName.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Обязательная переменная окружения не задана: " + ENV_FILE_NAME
            );
        }

        //Чтение порта
        int tempPort = DEFAULT_PORT;
        String portStr = System.getenv(ENV_PORT);

        if (portStr != null && !portStr.trim().isEmpty()) {
            try {
                tempPort = Integer.parseInt(portStr.trim());
            } catch (NumberFormatException e) {
                System.err.println("Неверный порт: " + portStr +
                        ", используется порт по умолчанию: " + DEFAULT_PORT);
            }
        }

        this.port = tempPort;
    }

    public String getFileName() {
        return fileName;
    }

    public int getPort() {
        return port;
    }
}