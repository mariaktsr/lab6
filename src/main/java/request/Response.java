package request;

//Результат выполнения команды

public class Response {
    private final boolean success;
    private final String message;
    private final Object data;

    public Response(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static Response success(String message) {
        return new Response(true, message, null);
    }
    public static Response success(String message, Object data) {
        return new Response(true, message, data);
    }
    public static Response error(String message) {
        return new Response(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Response{success=" + success + ", message='" + message + "'}";
    }
}