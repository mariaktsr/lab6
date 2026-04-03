package client.console;

import common.commands.CommandType;
import common.model.HumanBeing;
import common.request.Request;
import client.validation.Validation;
import client.validation.ClientValidationFactory;

import java.util.Scanner;

//Чтения команд из консоли

public class ConsoleReader {

    private final Scanner scanner;
    private final InputHelper inputHelper;
    private final ClientValidationFactory validationFactory;

    public ConsoleReader(Scanner scanner) {
        this.scanner = scanner;
        this.inputHelper = new InputHelper(scanner);
        this.validationFactory = new ClientValidationFactory();
    }

    //Считывает команду от пользователя
    public Request readCommand() {
        System.out.print("\n> ");
        String line = scanner.nextLine().trim();

        if (line.isEmpty()) {
            return null;
        }

        String[] tokens = line.split("\\s+", 2);
        String cmdName = tokens[0].toLowerCase();

        try {
            CommandType type = CommandType.valueOf(cmdName.toUpperCase());

            if (type.isServerOnly()) {
                System.err.println("Команда '" + type + "' доступна только на сервере");
                return null;
            }

            String[] args;
            if (tokens.length > 1) {
                args = new String[]{tokens[1]};
            } else {
                args = new String[0];
            }

            Validation<String[]> validator = validationFactory.getValidator(type);
            if (validator != null) {
                Validation.ValidationError error = validator.validate(args);
                if (error != null) {
                    System.err.println(error.getMessage());
                    return null;
                }
            }

            if (type.requiresData()) {
                return readComplexCommand(type, args);
            }

            return new Request(type, args);

        } catch (IllegalArgumentException e) {
            System.err.println("Неизвестная команда: " + cmdName);
            System.err.println("Введите 'help' для списка доступных команд");
            return null;
        }
    }

    //Читает сложные команды с вводом данных HumanBeing
    private Request readComplexCommand(CommandType type, String[] args) {
        try {
            Long existingId = null;

            if (type == CommandType.UPDATE) {
                existingId = Long.parseLong(args[0].trim());
            } else if (type == CommandType.INSERT_AT) {
                int index = Integer.parseInt(args[0].trim());
                if (index < 0) {
                    System.err.println("Индекс не может быть отрицательным");
                    return null;
                }
                args = new String[]{String.valueOf(index)};
            }

            System.out.println("\nВвод данных элемента:");
            HumanBeing human = inputHelper.readHumanBeing(existingId);

            return new Request(type, args, human);

        } catch (NumberFormatException e) {
            System.err.println("Ошибка парсинга аргумента: " + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка ввода: " + e.getMessage());
            return null;
        }
    }

    public InputHelper getInputHelper() {
        return inputHelper;
    }
}