package command.noarg;

import command.Command;
import handler.CollectionManager;
import request.Request;
import request.Response;

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
    public String getName() {
        return "info";
    }
    @Override
    public String getDescription() {
        return "вывести информацию о коллекции (тип, дата инициализации, количество элементов)";
    }
}
