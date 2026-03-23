package command.validation;

import request.Request;

import java.util.HashMap;
import java.util.Map;

public class ValidationFactory {

    private final Map<String, Validation<Request>> registry;

    public ValidationFactory() {
        this.registry = new HashMap<>();
        configureDefaults();
    }

    private void configureDefaults() {
        // Команды без аргументов
        registerNoArg("clear");
        registerNoArg("exit");
        registerNoArg("help");
        registerNoArg("info");
        registerNoArg("print_field_descending_mood");
        registerNoArg("save");
        registerNoArg("show");
        registerNoArg("sort");
        registerNoArg("sum_of_impact_speed");

        // Команды с числовым аргументом
        registerNumeric("remove_by_id");
        registerNumeric("remove_at");

        // Команды со строковым аргументом
        registerString("filter_contains_name");
        registerString("execute_script");

        // Команды со сложными данными
        registerComplex("add");
        registerComplex("update");
        registerComplex("insert_at");
    }

    private void registerNoArg(String cmd) {
        Validation<Request> chain = new ArgumentCountValidation(cmd, 0, new NoOpValidation<>());
        registry.put(cmd.toLowerCase(), chain);
    }

    private void registerNumeric(String cmd) {
        Validation<Request> chain = new ArgumentCountValidation(cmd, 1,
                new NotEmptyArgumentValidation(0,
                        new NumericArgumentValidation(0,
                                new NoOpValidation<>())));
        registry.put(cmd.toLowerCase(), chain);
    }

    private void registerString(String cmd) {
        Validation<Request> chain = new ArgumentCountValidation(cmd, 1,
                new NotEmptyArgumentValidation(0,
                        new NoOpValidation<>()));
        registry.put(cmd.toLowerCase(), chain);
    }

    private void registerComplex(String cmd) {
        registry.put(cmd.toLowerCase(), new NoOpValidation<>());
    }

    public Validation<Request> get(String commandName) {
        return registry.get(commandName.toLowerCase());
    }
}