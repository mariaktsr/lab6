package command;

import command.validation.Validation;
import command.validation.ValidationFactory;
import request.Request;

public class CommandValidator {

    private final ValidationFactory factory;

    public CommandValidator() {
        this.factory = new ValidationFactory();
    }

    public String validate(Request request) {
        String name = request.getCommandName();
        Validation<Request> validation = factory.get(name);

        if (validation == null) {
            return "Неизвестная команда: " + name;
        }

        Validation.ValidationError error = validation.validate(request);
        if (error != null) {
            return error.getMessage();
        } else {
            return null;
        }
    }
}