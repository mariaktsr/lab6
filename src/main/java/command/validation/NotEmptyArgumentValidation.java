package command.validation;

import request.Request;

//Проверяет, что аргумент не пустая строка.

public class NotEmptyArgumentValidation extends AbstractValidationDecorator<Request> {

    private final int argumentIndex;

    public NotEmptyArgumentValidation(int argumentIndex, Validation<Request> next) {
        super(next);
        this.argumentIndex = argumentIndex;
    }

    @Override
    public ValidationError validate(Request request) {
        String[] args = request.getArguments();
        if (argumentIndex < args.length) {
            String arg = args[argumentIndex];
            if (arg == null || arg.trim().isEmpty()) {
                return new ValidationError("Аргумент не может быть пустым");
            }
        }
        return super.validate(request);
    }
}