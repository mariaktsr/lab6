package command.onearg;

import command.Command;
import handler.CollectionManager;
import request.Request;
import request.Response;

//Команда фильтрации элементов по подстроке в имени

public class FilterContainsNameCommand implements Command {

    private final CollectionManager manager;

    public FilterContainsNameCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        String substring = request.getArguments()[0];

        String result = manager.filterContainsName(substring);

        if (result == null || result.trim().isEmpty()) {
            return Response.success("Элементы с именем, содержащим '" + substring + "', не найдены");
        }

        return Response.success(result);
    }
    @Override
    public String getName() {
        return "filter_contains_name";
    }
    @Override
    public String getDescription() {
        return "вывести элементы, значение поля name которых содержит заданную подстроку";
    }
}