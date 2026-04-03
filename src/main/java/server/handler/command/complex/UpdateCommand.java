package server.handler.command.complex;

import common.model.HumanBeing;
import common.request.Request;
import common.response.Response;
import server.handler.CollectionManager;
import server.handler.command.Command;

//Команда обновления элемента по ID

public class UpdateCommand implements Command {

    private final CollectionManager manager;

    public UpdateCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        String[] args = request.getArguments();

        if (args == null || args.length == 0) {
            return Response.error("Команда update требует ID элемента!");
        }

        Long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            return Response.error("ID должен быть числом!");
        }

        if (manager.findById(id).isEmpty()) {
            return Response.error("Элемент с ID=" + id + " не найден!");
        }

        HumanBeing updated = request.getData(HumanBeing.class);
        if (updated == null) {
            return Response.error("Внутренняя ошибка: объект HumanBeing не передан");
        }

        updated.setId(id);

        boolean success = manager.update(id, updated);
        if (success) {
            return Response.success("Элемент с ID=" + id + " обновлён");
        } else {
            return Response.error("Не удалось обновить элемент");
        }
    }

    @Override
    public String getDescription() {
        return "обновить значение элемента коллекции, id которого равен заданному";
    }
}