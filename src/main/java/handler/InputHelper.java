package handler;

import java.util.Scanner;

//Вспомогательный класс для ввода данных с консоли с валидацией

public class InputHelper {

    private static final Scanner scanner = new Scanner(System.in);

    public static String readString(String prompt, boolean allowEmpty) {
        boolean validInput = false;
        String result = "";

        while (!validInput) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty() || allowEmpty) {
                result = line;
                validInput = true;
            } else {
                System.out.println("  Поле не может быть пустым");
            }
        }
        return result;
    }

    public static double readDouble(String prompt) {
        boolean validInput = false;
        double result = 0.0;

        while (!validInput) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                result = Double.parseDouble(line);
                validInput = true;
            } catch (NumberFormatException e) {
                System.out.println("  Введите корректное число");
            }
        }
        return result;
    }

    public static long readLong(String prompt, Long min) {
        boolean validInput = false;
        long result = 0;

        while (!validInput) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                long value = Long.parseLong(line);
                if (min != null && value <= min) {
                    System.out.println("  Значение должно быть больше " + min);
                } else {
                    result = value;
                    validInput = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("  Введите корректное целое число");
            }
        }
        return result;
    }

    public static boolean readBoolean(String prompt) {
        boolean validInput = false;
        boolean result = false;

        while (!validInput) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().toLowerCase();

            if (line.equals("true")) {
                result = true;
                validInput = true;
            } else if (line.equals("false")) {
                result = false;
                validInput = true;
            } else {
                System.out.println("  Введите true или false");
            }
        }
        return result;
    }

    public static <T extends Enum<T>> T readEnum(String prompt, Class<T> enumClass) {
        boolean validInput = false;
        T result = null;

        while (!validInput) {
            System.out.print(prompt);
            System.out.print("(");
            T[] constants = enumClass.getEnumConstants();
            for (int i = 0; i < constants.length; i++) {
                System.out.print(constants[i].name());
                if (i < constants.length - 1) System.out.print(", ");
            }
            System.out.print("): ");

            String input = scanner.nextLine().trim().toUpperCase();
            try {
                result = Enum.valueOf(enumClass, input);
                validInput = true;
            } catch (IllegalArgumentException e) {
                System.out.println("  Недопустимое значение");
            }
        }
        return result;
    }
}