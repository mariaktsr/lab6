package command.invoker;

import command.Command;
import command.CommandFactory;
import request.Request;
import request.Response;

public class CommandInvoker {

    private final CommandFactory factory;

    public CommandInvoker(CommandFactory factory) {
        this.factory = factory;
    }

    public Response execute(Request request) {
        String commandName = request.getCommandName();

        Command command = factory.getCommand(commandName, request);

        if (command == null) {
            String validationError = factory.getValidator().validate(request);
            if (validationError != null) {
                return Response.error(validationError);
            }
            return Response.error(
                    "Неизвестная команда: '" + commandName + "'\n" +
                            "Введите 'help' для списка доступных команд"
            );
        }

        try {
            return command.execute(request);
        } catch (Exception e) {
            return Response.error("Ошибка выполнения: " + e.getMessage());
        }
    }
}