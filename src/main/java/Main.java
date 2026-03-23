import command.CommandFactory;
import command.CommandValidator;
import command.invoker.CommandInvoker;
import handler.CliHandler;
import handler.CollectionManager;
import handler.FileManager;
import handler.ScriptExecutor;

public class Main {
    public static void main(String[] args) {
        String fileName = "data.scv";

        CollectionManager collectionManager = new CollectionManager();
        FileManager fileManager = new FileManager(fileName);

        var loadedList = fileManager.load();
        for (var human : loadedList) {
            collectionManager.add(human);
        }

        ScriptExecutor scriptExecutor = new ScriptExecutor(collectionManager, fileManager);

        CommandValidator validator = new CommandValidator();
        CommandFactory commandFactory = new CommandFactory(
                collectionManager,
                fileManager,
                scriptExecutor,
                validator
        );

        CommandInvoker invoker = new CommandInvoker(commandFactory);

        CliHandler cliHandler = new CliHandler(invoker, collectionManager);

        scriptExecutor.setInvoker(invoker);
        scriptExecutor.setCliHandler(cliHandler);

        cliHandler.start();

        fileManager.save(collectionManager.getCollection());
    }
}