package command.noarg;

import command.Command;
import handler.CollectionManager;
import request.Request;
import request.Response;

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
    public String getName() {
        return "clear";
    }
    @Override
    public String getDescription() {
        return "очистить коллекцию";
    }
}