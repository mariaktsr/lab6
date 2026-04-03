package server.handler.command;

import common.commands.CommandType;
import server.handler.CollectionManager;
import server.handler.FileManager;
import server.handler.command.complex.AddCommand;
import server.handler.command.complex.InsertAtCommand;
import server.handler.command.complex.UpdateCommand;
import server.handler.command.noarg.*;
import server.handler.command.onearg.ExecuteScriptCommand;
import server.handler.command.onearg.FilterContainsNameCommand;
import server.handler.command.onearg.RemoveAtCommand;
import server.handler.command.onearg.RemoveByIdCommand;

public class CommandFactory {

    public static CommandInvoker createInvoker(
            CollectionManager cm,
            FileManager fm) {

        CommandInvoker invoker = new CommandInvoker();

        //No-arg команды
        invoker.register(CommandType.HELP, new HelpCommand(invoker::getCommands));
        invoker.register(CommandType.INFO, new InfoCommand(cm));
        invoker.register(CommandType.SHOW, new ShowCommand(cm));
        invoker.register(CommandType.CLEAR, new ClearCommand(cm));
        invoker.register(CommandType.SORT, new SortCommand(cm));
        invoker.register(CommandType.SUM_OF_IMPACT_SPEED, new SumOfImpactSpeedCommand(cm));
        invoker.register(CommandType.PRINT_FIELD_DESCENDING_MOOD, new PrintFieldDescendingMoodCommand(cm));

        //One-arg команды
        invoker.register(CommandType.REMOVE_BY_ID, new RemoveByIdCommand(cm));
        invoker.register(CommandType.REMOVE_AT, new RemoveAtCommand(cm));
        invoker.register(CommandType.FILTER_CONTAINS_NAME, new FilterContainsNameCommand(cm));
        invoker.register(CommandType.EXECUTE_SCRIPT, new ExecuteScriptCommand());

        //Complex команды
        invoker.register(CommandType.ADD, new AddCommand(cm));
        invoker.register(CommandType.UPDATE, new UpdateCommand(cm));
        invoker.register(CommandType.INSERT_AT, new InsertAtCommand(cm));

        //Server-only команды
        invoker.register(CommandType.SAVE_SERVER, new SaveServerCommand(cm, fm));

        return invoker;
    }
}