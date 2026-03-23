package handler;

import model.*;

import java.io.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

//Менеджер файлов (отвечает за чтение и запись CSV файлов)

public class FileManager {

    private final String fileName;

    public FileManager(String fileName) {
        this.fileName = fileName;
    }

    //Загружает коллекцию из файла
    public List<HumanBeing> load() {
        List<HumanBeing> list = new ArrayList<>();

        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("Файл не найден, создаётся новая коллекция");
            return list;
        }

        try (FileReader reader = new FileReader(fileName);
             BufferedReader br = new BufferedReader(reader)) {

            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                try {
                    HumanBeing human = parseCsvLine(line);
                    list.add(human);
                } catch (Exception e) {
                    System.err.println("Ошибка в строке " + lineNumber + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }

        return list;
    }

    //Сохраняет коллекцию в файл
    public void save(Stack<HumanBeing> collection) {
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(fileName))) {

            for (int i = 0; i< collection.size(); i++) {
                HumanBeing human = collection.get(i);
                String csvLine = toCsvLine(human);
                bos.write((csvLine + "\n").getBytes("UTF-8"));
            }

            bos.flush();
            System.out.println("Коллекция сохранена в файл: " + fileName);

        } catch (IOException e) {
            System.err.println("Ошибка записи файла: " + e.getMessage());
            throw new RuntimeException("Не удалось сохранить файл", e);
        }
    }

     //Парсит CSV строку в объект HumanBeing
    private HumanBeing parseCsvLine(String line) {
        String[] parts = line.split(";", -1);

        if (parts.length < 10) {
            throw new IllegalArgumentException("Недостаточно полей");
        }

        Long id = Long.parseLong(parts[0].trim());
        String name = parts[1].trim();
        double x = Double.parseDouble(parts[2].trim());
        Long y = Long.parseLong(parts[3].trim());
        ZonedDateTime creationDate = ZonedDateTime.parse(parts[4].trim());
        Boolean realHero = Boolean.parseBoolean(parts[5].trim());
        boolean hasToothpick = Boolean.parseBoolean(parts[6].trim());
        Long impactSpeed = Long.parseLong(parts[7].trim());
        WeaponType weaponType = WeaponType.valueOf(parts[8].trim().toUpperCase());
        Mood mood = Mood.valueOf(parts[9].trim().toUpperCase());

        Car car = null;
        if (parts.length > 10 && !parts[10].trim().isEmpty()) {
            car = new Car(parts[10].trim());
        }

        return new HumanBeing(
                id, name, new Coordinates(x, y), creationDate,
                realHero, hasToothpick, impactSpeed,
                weaponType, mood, car
        );
    }

    //Преобразует HumanBeing в CSV строку
    private String toCsvLine(HumanBeing human) {
        StringBuilder sb = new StringBuilder();

        sb.append(human.getId()).append(";");
        sb.append(human.getName()).append(";");
        sb.append(human.getCoordinates().getX()).append(";");
        sb.append(human.getCoordinates().getY()).append(";");
        sb.append(human.getCreationDate()).append(";");
        sb.append(human.getRealHero()).append(";");
        sb.append(human.isHasToothpick()).append(";");
        sb.append(human.getImpactSpeed()).append(";");
        sb.append(human.getWeaponType()).append(";");
        sb.append(human.getMood()).append(";");
        if (human.getCar() != null && human.getCar().getName() != null) {
            sb.append(human.getCar().getName());
        }

        return sb.toString();
    }

    public String getFileName() {
        return fileName;
    }
}