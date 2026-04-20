package server.handler;

import common.commands.CommandType;
import common.model.CommandDescriptor;
import common.request.Request;
import common.response.Response;
import server.handler.command.CommandInvoker;

import java.io.Serializable;
import java.util.Map;

//Обработчик команд на сервере

public class CommandHandler {

    private final CommandInvoker invoker;
    private final CommandRegistry registry;

    public CommandHandler(CollectionManager cm, FileManager fm) {
        this.registry = new CommandRegistry(cm, fm);
        this.invoker = registry.createInvoker();
    }

    public Response handle(Request request, boolean isFromServerConsole) {
        CommandType type = request.getCommandType();

        if (type == CommandType.GET_COMMANDS_METADATA) {
            Map<String, CommandDescriptor> clientCommands = registry.getClientDescriptors();
            return Response.success("Метаданы синхронизированы", (Serializable) clientCommands);
        }

        if (registry.isServerOnly(type) && !isFromServerConsole) {
            return Response.error("Команда '" + type.name().toLowerCase() + "' доступна только на сервере");
        }

        return invoker.execute(request);
    }
}