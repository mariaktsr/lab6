package server.handler.command.noarg;

import common.request.Request;
import common.response.Response;
import server.handler.CollectionManager;
import server.handler.command.Command;

//Команда очистки коллекции

public class ClearCommand implements Command {

    private final CollectionManager manager;

    public ClearCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        manager.clear();
        return Response.success("Коллекция очищена");
    }

    @Override
    public String getDescription() {
        return "очистить коллекцию";
    }
}