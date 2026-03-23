package command.validation;

import request.Request;

//Проверяет, что аргумент — число

public class NumericArgumentValidation extends AbstractValidationDecorator<Request> {

    private final int argumentIndex;

    public NumericArgumentValidation(int argumentIndex, Validation<Request> next) {
        super(next);
        this.argumentIndex = argumentIndex;
    }

    @Override
    public ValidationError validate(Request request) {
        String[] args = request.getArguments();
        if (argumentIndex < args.length) {
            try {
                Long.parseLong(args[argumentIndex]);
            } catch (NumberFormatException e) {
                return new ValidationError("Аргумент должен быть числом");
            }
        }
        return super.validate(request);
    }
}