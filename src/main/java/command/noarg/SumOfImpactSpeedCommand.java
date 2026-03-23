package command.noarg;

import command.Command;
import handler.CollectionManager;
import request.Request;
import request.Response;

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
    public String getName() {
        return "sum_of_impact_speed";
    }
    @Override
    public String getDescription() {
        return "вывести сумму значений поля impactSpeed для всех элементов коллекции";
    }
}