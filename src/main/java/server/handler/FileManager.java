package server.handler;

import common.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

//Менеджер файлов (отвечает за чтение и запись CSV файлов)

public class FileManager {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    private static final String DELIMITER = ";";

    private final String fileName;

    public FileManager(String fileName) {
        this.fileName = fileName;
    }

    public List<HumanBeing> load() {
        List<HumanBeing> list = new ArrayList<>();

        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("Файл не найден, создаётся новая коллекция: " + fileName);
            return list;
        }

        if (!file.canRead()) {
            System.err.println("Ошибка: нет прав на чтение файла " + fileName);
            return list;
        }

        try (FileReader reader = new FileReader(fileName);
             BufferedReader br = new BufferedReader(reader)) {

            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                try {
                    HumanBeing human = parseCsvLine(line);
                    list.add(human);
                } catch (Exception e) {
                    System.err.println("Ошибка в строке " + lineNumber + ": " + e.getMessage());
                }
            }

            System.out.println("Загружено элементов: " + list.size());

        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }

        return list;
    }

    public void save(Stack<HumanBeing> collection) throws IOException {
        File file = new File(fileName);

        if (file.exists() && !file.canWrite()) {
            throw new IOException("Нет прав на запись в файл: " + fileName);
        }

        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(fileName))) {

            for (HumanBeing human : collection) {
                String csvLine = toCsvLine(human);
                bos.write((csvLine + "\n").getBytes(StandardCharsets.UTF_8));
            }

            bos.flush();
            System.out.println("Коллекция сохранена в файл: " + fileName);

        } catch (IOException e) {
            System.err.println("Ошибка записи файла: " + e.getMessage());
            throw e;
        }
    }

    //Парсит одну CSV-строку в объект HumanBeing
    private HumanBeing parseCsvLine(String line) {
        String[] parts = line.split(DELIMITER, -1);

        if (parts.length < 10) {
            throw new IllegalArgumentException("Недостаточно полей (ожидалось 10, получено " + parts.length + ")");
        }

        try {
            Long id = Long.parseLong(parts[0].trim());
            String name = unescapeCsv(parts[1].trim());
            double x = Double.parseDouble(parts[2].trim());
            Long y = Long.parseLong(parts[3].trim());
            ZonedDateTime creationDate = ZonedDateTime.parse(parts[4].trim(), DATE_FORMATTER);
            Boolean realHero = Boolean.parseBoolean(parts[5].trim());
            boolean hasToothpick = Boolean.parseBoolean(parts[6].trim());
            Long impactSpeed = Long.parseLong(parts[7].trim());
            WeaponType weaponType = WeaponType.valueOf(parts[8].trim().toUpperCase());
            Mood mood = Mood.valueOf(parts[9].trim().toUpperCase());

            Car car = null;
            if (parts.length > 10 && !parts[10].trim().isEmpty()) {
                car = new Car(unescapeCsv(parts[10].trim()));
            }

            return new HumanBeing(
                    id, name, new Coordinates(x, y), creationDate,
                    realHero, hasToothpick, impactSpeed,
                    weaponType, mood, car
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка парсинга: " + e.getMessage(), e);
        }
    }

    //Преобразует объект HumanBeing в CSV-строку
    private String toCsvLine(HumanBeing human) {
        StringBuilder sb = new StringBuilder();

        sb.append(human.getId()).append(DELIMITER);
        sb.append(escapeCsv(human.getName())).append(DELIMITER);
        sb.append(human.getCoordinates().getX()).append(DELIMITER);
        sb.append(human.getCoordinates().getY()).append(DELIMITER);
        sb.append(human.getCreationDate().format(DATE_FORMATTER)).append(DELIMITER);
        sb.append(human.getRealHero()).append(DELIMITER);
        sb.append(human.isHasToothpick()).append(DELIMITER);
        sb.append(human.getImpactSpeed()).append(DELIMITER);
        sb.append(human.getWeaponType()).append(DELIMITER);
        sb.append(human.getMood()).append(DELIMITER);

        if (human.getCar() != null && human.getCar().getName() != null) {
            sb.append(escapeCsv(human.getCar().getName()));
        }

        return sb.toString();
    }

    //Экранирует специальные символы для CSV
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(DELIMITER) || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    //Деэкранирует CSV-строку
    private String unescapeCsv(String value) {
        if (value == null || value.isEmpty()) return "";
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1).replace("\"\"", "\"");
        }
        return value;
    }


    public String getFileName() {
        return fileName;
    }
    public boolean fileExists() {
        return new File(fileName).exists();
    }
}