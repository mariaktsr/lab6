package server.handler.command.onearg;
import server.handler.command.Command;
import common.request.Request;
import common.response.Response;
import server.handler.CollectionManager;

//Команда удаления элемента по ID

public class RemoveByIdCommand implements Command {

    private final CollectionManager manager;

    public RemoveByIdCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        String[] args = request.getArguments();

        if (args == null || args.length < 1) {
            return Response.error("Команда требует ID");
        }

        Long id;
        try {
            id = Long.parseLong(args[0].trim());
        } catch (NumberFormatException e) {
            return Response.error("ID должен быть числом");
        }

        boolean removed = manager.removeById(id);
        if (removed) {
            return Response.success("Элемент с ID " + id + " удалён");
        } else {
            return Response.error("Элемент с ID " + id + " не найден");
        }
    }

    @Override
    public String getDescription() {
        return "удалить элемент из коллекции по его id";
    }
}