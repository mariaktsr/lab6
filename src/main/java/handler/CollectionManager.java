package handler;

import model.HumanBeing;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

//Менеджер коллекции (управляет коллекцией объектов HumanBeing)

public class CollectionManager {

    private final Stack<HumanBeing> collection;
    private final String initializationDate;
    private final AtomicLong idGenerator;

    public CollectionManager() {
        this.collection = new Stack<>();
        this.initializationDate = java.time.ZonedDateTime.now().toString();
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
        for (int i = 0; i < collection.size(); i++) {
            HumanBeing current = collection.get(i);
            if (current.getId().equals(id)) {
                collection.set(i, updated);
                return true;
            }
        }
        return false;
    }

    //Удаляет элемент по ID
    public boolean removeById(Long id) {
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).getId().equals(id)) {
                collection.remove(i);
                return true;
            }
        }
        return false;
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
        List<HumanBeing> list = new ArrayList<>(collection);
        Collections.sort(list);
        collection.clear();
        for (int i = 0; i < list.size(); i++) {
            collection.push(list.get(i));
        }
    }

    //Находит элемент по ID
    public HumanBeing findById(Long id) {
        for (int i = 0; i< collection.size(); i++) {
            HumanBeing human = collection.get(i);
            if (human.getId().equals(id)) {
                return human;
            }
        }
        return null;
    }

    //Возвращает информацию о коллекции
    public String getInfo() {
        return "Тип коллекции: Stack<HumanBeing>\n" +
                "Дата инициализации: " + initializationDate + "\n" +
                "Количество элементов: " + collection.size();
    }

    //Возвращает все элементы в виде строки
    public String showAll() {
        if (collection.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < collection.size(); i++) {
            HumanBeing human = collection.get(i);
            sb.append(human.toString()).append("\n");
        }
        return sb.toString();
    }

    //Считает сумму impactSpeed
    public long sumOfImpactSpeed() {
        long sum = 0;
        for (int i = 0; i < collection.size(); i++) {
            HumanBeing human = collection.get(i);
            if (human.getImpactSpeed() != null) {
                sum += human.getImpactSpeed();
            }
        }
        return sum;
    }

    //Фильтрует элементы по подстроке в имени
    public String filterContainsName(String substring) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < collection.size(); i++) {
             HumanBeing human = collection.get(i);
            if (human.getName().contains(substring)) {
                sb.append(human.toString()).append("\n");
            }
        }
        return sb.toString();
    }

    //Выводит значения mood в порядке убывания
    public String printMoodDescending() {
        if (collection.isEmpty()) {
            return "";
        }

        List<HumanBeing> list = new ArrayList<>(collection);
        list.sort(new Comparator<HumanBeing>() {
            @Override
            public int compare(HumanBeing h1, HumanBeing h2) {
                return h2.getMood().compareTo(h1.getMood());
            }
        });

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            HumanBeing human = list.get(i);
            sb.append(human.getMood()).append("\n");
        }
        return sb.toString();
    }

    //Генерирует уникальный ID
    public Long generateId() {
        return idGenerator.getAndIncrement();
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
}
