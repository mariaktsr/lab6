package command.noarg;

import command.Command;
import handler.CollectionManager;
import handler.FileManager;
import request.Request;
import request.Response;

//Команда сохранения коллекции в файл

public class SaveCommand implements Command {

    private final CollectionManager manager;
    private final FileManager fileManager;

    public SaveCommand(CollectionManager manager, FileManager fileManager) {
        this.manager = manager;
        this.fileManager = fileManager;
    }

    @Override
    public Response execute(Request request) {
        try {
            fileManager.save(manager.getCollection());
            return Response.success("Коллекция сохранена в файл");
        } catch (Exception e) {
            return Response.error("Ошибка при сохранении: " + e.getMessage());
        }
    }
    @Override
    public String getName() {
        return "save";
    }
    @Override
    public String getDescription() {
        return "сохранить коллекцию в файл";
    }
}