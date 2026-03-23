package request;

//Запрос от пользователя

public class Request {
    private final String commandName;
    private final String[] arguments;
    private final Object data;

    public Request(String commandName, String[] arguments) {
        this(commandName, arguments, null);
    }

    public Request(String commandName, String[] arguments, Object data) {
        this.commandName = commandName;
        if (arguments != null) {
            this.arguments = arguments;
        } else {
            this.arguments = new String[0];
        }
        this.data = data;
    }

    public String getCommandName() { return commandName; }
    public String[] getArguments() { return arguments; }
    public <T> T getData(Class<T> type) {
        if (type.isInstance(data)) {
            return type.cast(data);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Request{command='" + commandName + "', args=" + arguments.length + ", hasData=" + (data != null) + "}";
    }
}