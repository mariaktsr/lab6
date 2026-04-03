package server.handler;

import common.model.HumanBeing;
import common.model.Mood;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

//Менеджер коллекции (управляет коллекцией объектов HumanBeing)

public class CollectionManager {

    private final Stack<HumanBeing> collection;
    private final String initializationDate;
    private final AtomicLong idGenerator;

    public CollectionManager() {
        this.collection = new Stack<>();
        this.initializationDate = ZonedDateTime.now().toString();
        this.idGenerator = new AtomicLong(1);
    }

    //Добавляет элемент в коллекцию
    public void add(HumanBeing human) {
        collection.push(human);
        updateMaxId(human.getId());
    }

    //Вставляет элемент в указанную позицию
    public void insertAt(int index, HumanBeing human) {
        if (index < 0 || index > collection.size()) {
            throw new IndexOutOfBoundsException(
                    "Индекс вне диапазона (0-" + collection.size() + ")"
            );
        }
        collection.add(index, human);
        updateMaxId(human.getId());
    }

    //Обновляет элемент по ID
    public boolean update(Long id, HumanBeing updated) {
        return collection.stream()
                .filter(h -> h.getId().equals(id))
                .findFirst()
                .map(existing -> {
                    int index = collection.indexOf(existing);
                    collection.set(index, updated);
                    return true;
                })
                .orElse(false);
    }

    //Удаляет элемент по ID
    public boolean removeById(Long id) {
        return collection.stream()
                .filter(h -> h.getId().equals(id))
                .findFirst()
                .map(collection::remove)
                .orElse(false);
    }

    //Удаляет элемент по индексу
    public void removeAt(int index) {
        if (index < 0 || index >= collection.size()) {
            throw new IndexOutOfBoundsException(
                    "Индекс вне диапазона (0-" + (collection.size() - 1) + ")"
            );
        }
        collection.remove(index);
    }

    //Очищает коллекцию
    public void clear() {
        collection.clear();
    }

    //Сортирует коллекцию
    public void sort() {
        List<HumanBeing> sortedList = collection.stream()
                .sorted()
                .collect(Collectors.toList());

        collection.clear();
        sortedList.forEach(collection::add);
    }

    //Находит элемент по ID
    public List<HumanBeing> getSortedByName() {
        return collection.stream()
                .sorted(Comparator.comparing(HumanBeing::getName))
                .collect(Collectors.toList());
    }

    //Возвращает информацию о коллекции
    public Optional<HumanBeing> findById(Long id) {
        return collection.stream()
                .filter(h -> h.getId().equals(id))
                .findFirst();
    }

    //Возвращает информацию о коллекции
    public String getInfo() {
        return String.format(
                "Тип коллекции: Stack<HumanBeing>%n" +
                        "Дата инициализации: %s%n" +
                        "Количество элементов: %d",
                initializationDate,
                collection.size()
        );
    }

    //Возвращает все элементы в виде строки
    public String showAll() {
        if (collection.isEmpty()) {
            return "Коллекция пуста";
        }

        return collection.stream()
                .map(HumanBeing::toString)
                .collect(Collectors.joining("\n"));
    }

    //Возвращает все элементы, отсортированные по имени
        public String showAllSortedByName() {
        if (collection.isEmpty()) {
            return "Коллекция пуста";
        }

        return getSortedByName().stream()
                .map(HumanBeing::toString)
                .collect(Collectors.joining("\n"));
    }

    //Считает сумму impactSpeed
    public long sumOfImpactSpeed() {
        return collection.stream()
                .map(HumanBeing::getImpactSpeed)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();
    }

    //Фильтрует элементы по подстроке в имени
    public List<HumanBeing> filterContainsName(String substring) {
        return collection.stream()
                .filter(h -> h.getName().contains(substring))
                .collect(Collectors.toList());
    }

    //Фильтрует элементы по подстроке в имени
    public String filterContainsNameAsString(String substring) {
        List<HumanBeing> filtered = filterContainsName(substring);

        if (filtered.isEmpty()) {
            return "Элементы не найдены";
        }

        return filtered.stream()
                .map(HumanBeing::toString)
                .collect(Collectors.joining("\n"));
    }

    //Выводит значения mood в порядке убывания
    public String printMoodDescending() {
        if (collection.isEmpty()) {
            return "Коллекция пуста";
        }

        return collection.stream()
                .map(HumanBeing::getMood)
                .sorted(Comparator.reverseOrder())
                .distinct()
                .map(Enum::name)
                .collect(Collectors.joining("\n"));
    }

    //Генерирует уникальный ID
    public Long generateId() {
        return idGenerator.getAndIncrement();
    }

    //Обновляет генератор ID на основе максимального ID в коллекции
    public void initializeIdGenerator(Collection<HumanBeing> loadedCollection) {
        long maxId = loadedCollection.stream()
                .map(HumanBeing::getId)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        idGenerator.set(maxId + 1);
    }

    //Обновляет максимальный ID
    private void updateMaxId(Long id) {
        long currentMax = idGenerator.get() - 1;
        if (id > currentMax) {
            idGenerator.set(id + 1);
        }
    }

    public int getSize() {
        return collection.size();
    }
    public Stack<HumanBeing> getCollection() {
        return collection;
    }

    //Проверяет, пуста ли коллекция
    public boolean isEmpty() {
        return collection.isEmpty();
    }

    //Проверяет уникальность ID
    public boolean isIdUnique(Long id) {
        return collection.stream()
                .noneMatch(h -> h.getId().equals(id));
    }
}