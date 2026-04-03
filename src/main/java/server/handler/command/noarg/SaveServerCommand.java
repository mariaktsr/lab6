package server.handler.command.noarg;

import common.request.Request;
import common.response.Response;
import server.handler.CollectionManager;
import server.handler.FileManager;
import server.handler.command.Command;

import java.io.IOException;

//Команда сохранения коллекции в файл
//(доступна ТОЛЬКО на сервере)

public class SaveServerCommand implements Command {

    private final CollectionManager manager;
    private final FileManager fileManager;

    public SaveServerCommand(CollectionManager manager, FileManager fileManager) {
        this.manager = manager;
        this.fileManager = fileManager;
    }

    @Override
    public Response execute(Request request) {
        try {
            fileManager.save(manager.getCollection());
            return Response.success("Коллекция сохранена в файл: " + fileManager.getFileName());
        } catch (IOException e) {
            return Response.error("Ошибка при сохранении: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "сохранить коллекцию в файл (только сервер)";
    }
}