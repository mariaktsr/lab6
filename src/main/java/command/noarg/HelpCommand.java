package command.noarg;

import command.Command;
import command.CommandFactory;
import request.Request;
import request.Response;

//Команда вывода справки по доступным командам

public class HelpCommand implements Command {

    private final CommandFactory factory;

    public HelpCommand(CommandFactory factory) {
        this.factory = factory;
    }

    @Override
    public Response execute(Request request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Доступные команды:\n");
        sb.append("--------------------------------------------------------\n");

        for (String name : factory.getCommandNames()) {
            Command cmd = factory.getCommandByName(name);
            if (cmd != null) {
                sb.append("  ").append(name);
                for (int i = name.length(); i < 25; i++) {
                    sb.append(" ");
                }
                sb.append(" : ").append(cmd.getDescription()).append("\n");
            }
        }
        sb.append("--------------------------------------------------------\n");
        sb.append("Введите имя команды для выполнения.");

        return Response.success(sb.toString());
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "вывести справку по доступным командам";
    }
}