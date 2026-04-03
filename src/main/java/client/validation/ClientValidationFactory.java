package client.validation;

import common.commands.CommandType;

import java.util.HashMap;
import java.util.Map;

public class ClientValidationFactory {

    private final Map<CommandType, Validation<String[]>> validators;

    public ClientValidationFactory() {
        this.validators = new HashMap<>();
        configureValidators();
    }

    private void configureValidators() {
        //Команды без аргументов
        registerNoArg(CommandType.CLEAR);
        registerNoArg(CommandType.EXIT);
        registerNoArg(CommandType.HELP);
        registerNoArg(CommandType.INFO);
        registerNoArg(CommandType.PRINT_FIELD_DESCENDING_MOOD);
        registerNoArg(CommandType.SHOW);
        registerNoArg(CommandType.SORT);
        registerNoArg(CommandType.SUM_OF_IMPACT_SPEED);

        //Команды с числовым аргументом
        registerNumeric(CommandType.REMOVE_BY_ID);
        registerNumeric(CommandType.REMOVE_AT);

        //Команды со строковым аргументом
        //UPDATE: требует ID (число) + ввод HumanBeing
        registerString(CommandType.FILTER_CONTAINS_NAME);
        registerString(CommandType.EXECUTE_SCRIPT);

        //Команды со сложными данными
        validators.put(CommandType.UPDATE,
                new ArgumentCountValidation("update", 1,
                        new NumericArgumentValidation(0,
                                new NoOpValidation<>())));

        //INSERT_AT: требует индекс (число) + ввод HumanBeing
        validators.put(CommandType.INSERT_AT,
                new ArgumentCountValidation("insert_at", 1,
                        new NumericArgumentValidation(0,
                                new NoOpValidation<>())));

        //ADD: не требует аргументов, только ввод HumanBeing
        validators.put(CommandType.ADD, new NoOpValidation<>());
    }

    private void registerNoArg(CommandType type) {
        validators.put(type,
                new ArgumentCountValidation(type.name().toLowerCase(), 0,
                        new NoOpValidation<>()));
    }

    private void registerNumeric(CommandType type) {
        validators.put(type,
                new ArgumentCountValidation(type.name().toLowerCase(), 1,
                        new NotEmptyArgumentValidation(0,
                                new NumericArgumentValidation(0,
                                        new NoOpValidation<>()))));
    }

    private void registerString(CommandType type) {
        validators.put(type,
                new ArgumentCountValidation(type.name().toLowerCase(), 1,
                        new NotEmptyArgumentValidation(0,
                                new NoOpValidation<>())));
    }

    public Validation<String[]> getValidator(CommandType type) {
        return validators.get(type);
    }
}