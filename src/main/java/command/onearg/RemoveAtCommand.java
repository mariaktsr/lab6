package command.onearg;

import command.Command;
import handler.CollectionManager;
import request.Request;
import request.Response;

//Команда удаления элемента по индексу

public class RemoveAtCommand implements Command {

    private final CollectionManager manager;

    public RemoveAtCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        String substring = request.getArguments()[0];
        if (!isLong(substring)) {
            return Response.error("Индекс должен быть целым числом!");
        }

        int index = Integer.parseInt(substring);

        try {
            manager.removeAt(index);
            return Response.success("Элемент в позиции " + index + " удалён");
        } catch (IndexOutOfBoundsException e) {
            return Response.error("Индекс вне диапазона (0-" + (manager.getSize() - 1) + ")");
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
        return "remove_at";
    }
    @Override
    public String getDescription() {
        return "удалить элемент, находящийся в заданной позиции коллекции (index)";
    }
}