package command.noarg;

import command.Command;
import request.Request;
import request.Response;

//Команда завершения программы

public class ExitCommand implements Command {

    @Override
    public Response execute(Request request) {
        return Response.success("");
    }
    @Override
    public String getName() {
        return "exit";
    }
    @Override
    public String getDescription() {
        return "завершить программу (без сохранения в файл)";
    }
}