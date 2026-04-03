package server.handler.command.complex;

import common.model.HumanBeing;
import common.request.Request;
import common.response.Response;
import server.handler.CollectionManager;
import server.handler.command.Command;
import java.time.ZonedDateTime;

//Команда добавления нового элемента в коллекцию

public class AddCommand implements Command {

    private final CollectionManager manager;

    public AddCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        HumanBeing human = request.getData(HumanBeing.class);

        if (human == null) {
            return Response.error("Внутренняя ошибка: объект HumanBeing не передан");
        }

        //Автогенерация полей ТОЛЬКО на сервере
        human.setId(manager.generateId());
        human.setCreationDate(ZonedDateTime.now());

        manager.add(human);
        return Response.success("Элемент успешно добавлен с ID: " + human.getId());
    }

    @Override
    public String getDescription() {
        return "добавить новый элемент в коллекцию";
    }
}