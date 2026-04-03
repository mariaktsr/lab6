package server.handler.command.noarg;

import common.request.Request;
import common.response.Response;
import server.handler.CollectionManager;
import server.handler.command.Command;

//Команда вывода значений поля mood всех элементов в порядке убывания

public class PrintFieldDescendingMoodCommand implements Command {

    private final CollectionManager manager;

    public PrintFieldDescendingMoodCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        String result = manager.printMoodDescending();

        if (result == null || result.trim().isEmpty()) {
            return Response.success("Коллекция пуста");
        }

        return Response.success(result);
    }

    @Override
    public String getDescription() {
        return "вывести значения поля mood всех элементов в порядке убывания";
    }
}