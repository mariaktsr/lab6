package command.complex;

import command.Command;
import handler.CollectionManager;
import model.HumanBeing;
import request.Request;
import request.Response;

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

        manager.add(human);
        return Response.success("Элемент успешно добавлен с ID: " + human.getId());
    }
    @Override
    public String getName() {
        return "add";
    }
    @Override
    public String getDescription() {
        return "добавить новый элемент в коллекцию";
    }
}