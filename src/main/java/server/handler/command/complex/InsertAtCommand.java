package server.handler.command.complex;

import common.model.HumanBeing;
import common.request.Request;
import common.response.Response;
import server.handler.CollectionManager;
import server.handler.command.Command;
import java.time.ZonedDateTime;

//Команда вставки элемента в заданную позицию

public class InsertAtCommand implements Command {

    private final CollectionManager manager;

    public InsertAtCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        String[] args = request.getArguments();

        if (args == null || args.length == 0) {
            return Response.error("Команда требует аргумент (индекс)!");
        }

        int index;
        try {
            index = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return Response.error("Индекс должен быть целым числом!");
        }

        if (index < 0 || index > manager.getSize()) {
            return Response.error("Индекс вне диапазона (0-" + manager.getSize() + ")");
        }

        HumanBeing human = request.getData(HumanBeing.class);
        if (human == null) {
            return Response.error("Внутренняя ошибка: объект HumanBeing не передан");
        }

        //Автогенерация полей ТОЛЬКО на сервере
        human.setId(manager.generateId());
        human.setCreationDate(ZonedDateTime.now());

        manager.insertAt(index, human);
        return Response.success("Элемент вставлен в позицию " + index + " с ID: " + human.getId());
    }

    @Override
    public String getDescription() {
        return "добавить новый элемент в заданную позицию";
    }
}