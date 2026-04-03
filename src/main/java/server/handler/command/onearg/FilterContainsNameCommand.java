package server.handler.command.onearg;

import common.request.Request;
import common.response.Response;
import server.handler.CollectionManager;
import server.handler.command.Command;
import java.util.List;

/**
 * Команда фильтрации элементов по подстроке в имени.
 */
public class FilterContainsNameCommand implements Command {

    private final CollectionManager collectionManager;

    public FilterContainsNameCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        String[] args = request.getArguments();

        if (args == null || args.length < 1) {
            return Response.error("Команда требует подстроку для поиска");
        }

        String substring = args[0];
        List<common.model.HumanBeing> result = collectionManager.filterContainsName(substring);

        if (result.isEmpty()) {
            return Response.success("Элементы с именем, содержащим '" + substring + "', не найдены");
        }

        // Форматируем вывод
        String output = result.stream()
                .map(common.model.HumanBeing::toString)
                .collect(java.util.stream.Collectors.joining("\n"));

        return Response.success(output);
    }

    @Override
    public String getDescription() {
        return "вывести элементы, значение поля name которых содержит заданную подстроку";
    }
}