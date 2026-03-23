package command.noarg;

import command.Command;
import handler.CollectionManager;
import request.Request;
import request.Response;

//Команда вывода значений поля mood всех элементов в порядке убывания

public class PrintFieldDescendingMoodCommand implements Command {

    private final CollectionManager manager;

    public PrintFieldDescendingMoodCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        String result = manager.printMoodDescending();

        if (result == null || result.trim().isEmpty()) {
            return Response.success("Коллекция пуста");
        }

        return Response.success(result);
    }
    @Override
    public String getName() {
        return "print_field_descending_mood";
    }
    @Override
    public String getDescription() {
        return "вывести значения поля mood всех элементов в порядке убывания";
    }
}