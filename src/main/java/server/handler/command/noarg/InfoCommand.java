package server.handler.command.noarg;

import common.request.Request;
import common.response.Response;
import server.handler.CollectionManager;
import server.handler.command.Command;

//Команда вывода информации о коллекции

public class InfoCommand implements Command {

    private final CollectionManager manager;

    public InfoCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        String info = manager.getInfo();
        return Response.success(info);
    }

    @Override
    public String getDescription() {
        return "вывести информацию о коллекции (тип, дата инициализации, количество элементов)";
    }
}