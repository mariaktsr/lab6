package server.handler.command.noarg;

import common.request.Request;
import common.response.Response;
import server.handler.CollectionManager;
import server.handler.command.Command;

//Команда вывода суммы значений поля impactSpeed

public class SumOfImpactSpeedCommand implements Command {

    private final CollectionManager manager;

    public SumOfImpactSpeedCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        long sum = manager.sumOfImpactSpeed();
        return Response.success("Сумма значений impactSpeed: " + sum);
    }

    @Override
    public String getDescription() {
        return "вывести сумму значений поля impactSpeed для всех элементов коллекции";
    }
}