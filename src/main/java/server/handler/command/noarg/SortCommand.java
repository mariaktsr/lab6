package server.handler.command.noarg;

import common.request.Request;
import common.response.Response;
import server.handler.CollectionManager;
import server.handler.command.Command;

//Команда сортировки коллекции

public class SortCommand implements Command {

    private final CollectionManager manager;

    public SortCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        manager.sort();
        return Response.success("Коллекция отсортирована");
    }

    @Override
    public String getDescription() {
        return "отсортировать коллекцию в естественном порядке";
    }
}