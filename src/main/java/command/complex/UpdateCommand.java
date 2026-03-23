package command.complex;

import command.Command;
import handler.CollectionManager;
import model.HumanBeing;
import request.Request;
import request.Response;

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

        if (manager.findById(id) == null) {
            return Response.error("Элемент с ID=" + id + " не найден!");
        }

        HumanBeing updated = (HumanBeing) request.getData(HumanBeing.class);
        if (updated == null) {
            return Response.error("Внутренняя ошибка: объект HumanBeing не передан");
        }

        boolean success = manager.update(id, updated);
        if (success) {
            return Response.success("Элемент с ID=" + id + " обновлён");
        } else {
            return Response.error("Не удалось обновить элемент");
        }
    }
    @Override
    public String getName() {
        return "update";
    }
    @Override
    public String getDescription() {
        return "обновить значение элемента коллекции, id которого равен заданному";
    }
}