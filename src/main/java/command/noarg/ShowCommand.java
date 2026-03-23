package command.noarg;

import command.Command;
import handler.CollectionManager;
import request.Request;
import request.Response;

//Команда вывода всех элементов коллекции

public class ShowCommand implements Command {

    private final CollectionManager manager;

    public ShowCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        String result = manager.showAll();

        if (result == null || result.trim().isEmpty()) {
            return Response.success("Коллекция пуста");
        }

        return Response.success(result);
    }
    @Override
    public String getName() {
        return "show";
    }
    @Override
    public String getDescription() {
        return "вывести все элементы коллекции в строковом представлении";
    }
}