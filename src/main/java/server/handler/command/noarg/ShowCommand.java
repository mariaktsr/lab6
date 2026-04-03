package server.handler.command.noarg;

import common.request.Request;
import common.response.Response;
import server.handler.CollectionManager;
import server.handler.command.Command;

//Команда вывода всех элементов коллекции

public class ShowCommand implements Command {

    private final CollectionManager manager;

    public ShowCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        String result = manager.showAllSortedByName();

        if (result == null || result.trim().isEmpty()) {
            return Response.success("Коллекция пуста");
        }

        return Response.success(result);
    }

    @Override
    public String getDescription() {
        return "вывести все элементы коллекции в строковом представлении";
    }
}