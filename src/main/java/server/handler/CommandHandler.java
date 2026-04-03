package server.handler;

import common.commands.CommandType;
import common.request.Request;
import common.response.Response;
import server.handler.command.CommandFactory;
import server.handler.command.CommandInvoker;

//Обработчик команд на сервере

public class CommandHandler {

    private final CommandInvoker invoker;
    private final FileManager fileManager;

    //Создаёт обработчик команд
    public CommandHandler(CollectionManager collectionManager, FileManager fileManager) {
        this.fileManager = fileManager;
        this.invoker = CommandFactory.createInvoker(collectionManager, fileManager);
    }

    //Обрабатывает запрос от клиента
    public Response handle(Request request, boolean isFromServerConsole) {
        CommandType type = request.getCommandType();

        // Защита серверных команд (клиент не может их вызвать)
        if (type.isServerOnly() && !isFromServerConsole) {
            return Response.error("Команда '" + type + "' доступна только на сервере");
        }

        // Выполнение команды через инвокер
        return invoker.execute(request);
    }
}