package command.onearg;

import command.Command;
import handler.ScriptExecutor;
import request.Request;
import request.Response;

//Команда выполнения скрипта из файла

public class ExecuteScriptCommand implements Command {

    private final ScriptExecutor executor;

    public ExecuteScriptCommand(ScriptExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Response execute(Request request) {
        String substring = request.getArguments()[0];

        String fileName = substring.trim();

        if (fileName.isEmpty()) {
            return Response.error("Имя файла не может быть пустым!");
        }

        try {
            executor.executeScript(fileName);
            return Response.success("Скрипт выполнен: " + fileName);
        } catch (Exception e) {
            return Response.error("Ошибка при выполнении скрипта: " + e.getMessage());
        }
    }
    @Override
    public String getName() {
        return "execute_script";
    }
    @Override
    public String getDescription() {
        return "считать и исполнить скрипт из указанного файла";
    }
}