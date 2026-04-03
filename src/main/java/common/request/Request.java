package common.request;

import common.commands.CommandType;

import java.io.Serializable;

//Запрос от клиента к серверу

public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    private final CommandType commandType;
    private final String[] arguments;
    private final Serializable data;

    public Request(CommandType commandType, String[] arguments) {
        this(commandType, arguments, null);
    }

    public Request(CommandType commandType, String[] arguments, Serializable data) {
        if (commandType == null) {
            throw new IllegalArgumentException("Тип команды не может быть null");
        }
        this.commandType = commandType;
        if (arguments != null) {
            this.arguments = arguments;
        } else {
            this.arguments = new String[0];
        }
        this.data = data;
    }

    public CommandType getCommandType() {
        return commandType;
    }
    public String[] getArguments() {
        return arguments;
    }
    @SuppressWarnings("unchecked")
    public <T> T getData(Class<T> type) {
        if (data != null && type.isInstance(data)) {
            return (T) data;
        }
        return null;
    }

    public boolean hasData() {
        return data != null;
    }

    @Override
    public String toString() {
        return "Request{commandType=" + commandType + ", args=" + arguments.length + ", hasData=" + (data != null) + '}';
    }
}