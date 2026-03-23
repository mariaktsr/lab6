package handler;

import command.invoker.CommandInvoker;
import model.*;
import request.Request;
import request.Response;

import java.time.ZonedDateTime;
import java.util.Scanner;

public class CliHandler {

    private final CommandInvoker invoker;
    private final CollectionManager collectionManager;
    private final Scanner scanner;

    private boolean running = true;

    public CliHandler(CommandInvoker invoker, CollectionManager collectionManager) {
        this.invoker = invoker;
        this.collectionManager = collectionManager;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Приложение запущено. Введите 'help' для справки.");

        while (running) {
            System.out.print("\n> ");
            if (!scanner.hasNextLine()) {
                System.err.println("Входной поток закрыт. Завершение программы.");
                break;
            }
            String inputLine = scanner.nextLine().trim();

            if (inputLine.isEmpty()) {
                continue;
            }

            try {
                Request request = parseInput(inputLine);
                Response response = invoker.execute(request);
                printResponse(response);

                if ("exit".equalsIgnoreCase(request.getCommandName()) && response.isSuccess()) {
                    running = false;
                }

            } catch (IllegalArgumentException e) {
                System.err.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Непредвиденная ошибка: " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Программа завершена.");
        scanner.close();
    }

    private Request parseInput(String input) {
        String[] tokens = input.split("\\s+", 2);
        String commandName = tokens[0].toLowerCase();

        if ("add".equals(commandName)) {
            HumanBeing human = readHumanBeing(null);
            return new Request(commandName, new String[0], human);

        } else if ("update".equals(commandName)) {
            if (tokens.length < 2) {
                throw new IllegalArgumentException("Команда 'update' требует аргумент ID");
            }
            String idStr = tokens[1].trim().split("\\s+")[0];
            if (!isLong(idStr)) {
                throw new IllegalArgumentException("ID должен быть числом");
            }
            Long id = Long.parseLong(idStr);
            HumanBeing human = readHumanBeing(id);
            return new Request(commandName, new String[]{idStr}, human);

        } else if ("insert_at".equals(commandName)) {
            if (tokens.length < 2) {
                throw new IllegalArgumentException("Команда 'insert_at' требует аргумент index");
            }
            String indexStr = tokens[1].trim().split("\\s+")[0];
            if (!isLong(indexStr)) {
                throw new IllegalArgumentException("Индекс должен быть числом");
            }
            int index = Integer.parseInt(indexStr);
            HumanBeing human = readHumanBeing(null);
            return new Request(commandName, new String[]{String.valueOf(index)}, human);
        }

        String[] arguments = new String[0];
        if (tokens.length > 1) {
            arguments = new String[]{tokens[1]};
        }
        return new Request(commandName, arguments);
    }

    public Request parseRequestForScript(String commandName, String[] arguments) {
        System.out.println("  >> Требуется ввод данных для команды: " + commandName);

        if ("add".equals(commandName)) {
            HumanBeing human = readHumanBeing(null);
            return new Request(commandName, new String[0], human);

        } else if ("update".equals(commandName)) {
            if (arguments.length < 1) {
                throw new IllegalArgumentException("Команда 'update' требует аргумент ID");
            }
            String idStr = arguments[0].trim().split("\\s+")[0];
            if (!isLong(idStr)) {
                throw new IllegalArgumentException("ID должен быть числом");
            }
            Long id = Long.parseLong(idStr);
            HumanBeing human = readHumanBeing(id);
            return new Request(commandName, arguments, human);

        } else if ("insert_at".equals(commandName)) {
            if (arguments.length < 1) {
                throw new IllegalArgumentException("Команда 'insert_at' требует аргумент index");
            }
            String indexStr = arguments[0].trim().split("\\s+")[0];
            if (!isLong(indexStr)) {
                throw new IllegalArgumentException("Индекс должен быть числом");
            }
            int index = Integer.parseInt(indexStr);
            HumanBeing human = readHumanBeing(null);
            return new Request(commandName, new String[]{String.valueOf(index)}, human);
        }

        return new Request(commandName, arguments);
    }

    private HumanBeing readHumanBeing(Long existingId) {
        System.out.println("\n--- Ввод данных элемента ---");

        String name = InputHelper.readString("Введите имя: ", false);

        System.out.println("Введите координаты:");
        double x = InputHelper.readDouble("  X (double): ");
        long y = InputHelper.readLong("  Y (> -228): ", -228L);
        Coordinates coordinates = new Coordinates(x, y);

        boolean realHero = InputHelper.readBoolean("Реальный герой? (true/false): ");
        boolean hasToothpick = InputHelper.readBoolean("Есть зубочистка? (true/false): ");
        long impactSpeed = InputHelper.readLong("impactSpeed (> -428): ", -428L);

        WeaponType weaponType = InputHelper.readEnum("weaponType", WeaponType.class);
        Mood mood = InputHelper.readEnum("mood", Mood.class);

        Car car = readCar();

        Long id;
        if (existingId != null) {
            id = existingId;
        } else {
            id = collectionManager.generateId();
        }
        ZonedDateTime creationDate = ZonedDateTime.now();

        return new HumanBeing(
                id, name, coordinates, creationDate,
                realHero, hasToothpick, impactSpeed,
                weaponType, mood, car
        );
    }

    private Car readCar() {
        boolean inputCompleted = false;
        Car resultCar = null;

        while (!inputCompleted) {
            System.out.print("Ввести данные автомобиля? (y/n): ");
            String carChoice = InputHelper.readString("", false).trim().toLowerCase();

            if (carChoice.equals("y") || carChoice.equals("yes")) {
                String carName = InputHelper.readString("  Имя автомобиля (может быть пустым): ", true);
                if (carName.isEmpty()) {
                    resultCar = null;
                } else {
                    resultCar = new Car(carName);
                }
                inputCompleted = true;
            } else if (carChoice.equals("n") || carChoice.equals("no")) {
                resultCar = null;
                inputCompleted = true;

            } else {
                System.out.println("   Введите y (да) или n (нет)");
            }
        }

        return resultCar;
    }

    private void printResponse(Response response) {
        if (response == null) return;
        if (response.getMessage() != null && !response.getMessage().isEmpty()) {
            System.out.println(response.getMessage());
        }
        if (response.getData() != null) {
            String data = response.getData().toString();
            if (!data.isEmpty()) {
                System.out.println(data);
            }
        }
    }

    private boolean isLong(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void stop() { running = false; }
    public boolean isRunning() { return running; }
}