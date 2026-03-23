package command;

import command.complex.AddCommand;
import command.complex.InsertAtCommand;
import command.complex.UpdateCommand;
import command.noarg.*;
import command.onearg.ExecuteScriptCommand;
import command.onearg.FilterContainsNameCommand;
import command.onearg.RemoveAtCommand;
import command.onearg.RemoveByIdCommand;
import handler.CollectionManager;
import handler.FileManager;
import handler.ScriptExecutor;
import request.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommandFactory {

    private final Map<String, Command> commands;
    private final CommandValidator validator;

    public CommandFactory(
            CollectionManager cm,
            FileManager fm,
            ScriptExecutor se,
            CommandValidator validator
    ) {
        this.validator = validator;
        this.commands = new HashMap<>();
        registerAll(cm, fm, se);
    }

    private void registerAll(CollectionManager cm, FileManager fm, ScriptExecutor se) {
        register("help", new HelpCommand(this));
        register("info", new InfoCommand(cm));
        register("show", new ShowCommand(cm));
        register("clear", new ClearCommand(cm));
        register("sort", new SortCommand(cm));
        register("save", new SaveCommand(cm, fm));
        register("exit", new ExitCommand());
        register("sum_of_impact_speed", new SumOfImpactSpeedCommand(cm));
        register("print_field_descending_mood", new PrintFieldDescendingMoodCommand(cm));
        register("remove_by_id", new RemoveByIdCommand(cm));
        register("remove_at", new RemoveAtCommand(cm));
        register("filter_contains_name", new FilterContainsNameCommand(cm));
        register("execute_script", new ExecuteScriptCommand(se));
        register("add", new AddCommand(cm));
        register("update", new UpdateCommand(cm));
        register("insert_at", new InsertAtCommand(cm));
    }

    private void register(String name, Command cmd) {
        commands.put(name.toLowerCase(), cmd);
    }

    public Command getCommand(String name, Request request) {
        if (validator.validate(request) != null) return null;
        return commands.get(name.toLowerCase());
    }
    public Command getCommandByName(String name) {
        return commands.get(name.toLowerCase());
    }
    public Set<String> getCommandNames() {
        return commands.keySet();
    }

    public CommandValidator getValidator() { return validator; }

}