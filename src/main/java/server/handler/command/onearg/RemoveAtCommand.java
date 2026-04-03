package server.handler.command.onearg;
import server.handler.command.Command;
import common.request.Request;
import common.response.Response;
import server.handler.CollectionManager;

//Команда удаления элемента по индексу

public class RemoveAtCommand implements Command {

    private final CollectionManager collectionManager;

    public RemoveAtCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        String[] args = request.getArguments();

        if (args == null || args.length < 1) {
            return Response.error("Команда требует индекс");
        }

        int index;
        try {
            index = Integer.parseInt(args[0].trim());
        } catch (NumberFormatException e) {
            return Response.error("Индекс должен быть целым числом");
        }

        try {
            collectionManager.removeAt(index);
            return Response.success("Элемент в позиции " + index + " удалён");
        } catch (IndexOutOfBoundsException e) {
            return Response.error("Индекс вне диапазона (0-" + (collectionManager.getSize() - 1) + ")");
        }
    }

    @Override
    public String getDescription() {
        return "удалить элемент, находящийся в заданной позиции коллекции (index)";
    }
}