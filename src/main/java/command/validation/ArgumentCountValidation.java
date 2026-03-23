package command.validation;

import request.Request;

//Проверяет количество аргументов

public class ArgumentCountValidation extends AbstractValidationDecorator<Request> {

    private final String commandName;
    private final int expectedCount;

    public ArgumentCountValidation(String commandName, int expectedCount, Validation<Request> next) {
        super(next);
        this.commandName = commandName;
        this.expectedCount = expectedCount;
    }

    @Override
    public ValidationError validate(Request request) {
        String[] args = request.getArguments();
        if (args.length != expectedCount) {
            return new ValidationError(
                    String.format("'%s' требует %d аргумент(ов)", commandName, expectedCount)
            );
        }
        return super.validate(request);
    }
}