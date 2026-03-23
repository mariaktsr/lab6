package command.onearg;

import command.Command;
import handler.CollectionManager;
import request.Request;
import request.Response;

//Команда удаления элемента по ID

public class RemoveByIdCommand implements Command {

    private final CollectionManager manager;

    public RemoveByIdCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        String substring = request.getArguments()[0];
        if (!isLong(substring)) {
            return Response.error("ID должен быть числом!");
        }

        Long id = Long.parseLong(substring);
        boolean removed = manager.removeById(id);

        if (removed) {
            return Response.success("Элемент с ID " + id + " удалён");
        } else {
            return Response.error("Элемент с ID " + id + " не найден");
        }
    }

    protected boolean isLong(String arg) {
        try {
            Long.parseLong(arg);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getName() {
        return "remove_by_id";
    }
    @Override
    public String getDescription() {
        return "удалить элемент из коллекции по его id";
    }
}
