package command.noarg;

import command.Command;
import handler.CollectionManager;
import request.Request;
import request.Response;

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
    public String getName() {
        return "sort";
    }
    @Override
    public String getDescription() {
        return "отсортировать коллекцию в естественном порядке";
    }
}