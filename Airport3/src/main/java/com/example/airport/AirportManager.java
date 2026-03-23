package com.example.airport;
import org.apache.log4j.Logger;
import java.util.*;
import java.io.*;

public class AirportManager {
    static final Logger log = Logger.getLogger(AirportManager.class);
    private List<Aircraft> fleet = new ArrayList<>();

    public void performMaintenance() {
        System.out.println("\n--- Запуск планового технического обслуживания ---");
        boolean found = false;
        for (Aircraft a : fleet) {
            if (a instanceof Maintainable) { // Проверяем, поддерживает ли объект ТО
                ((Maintainable) a).performMaintenance();
                found = true;
            }
        }
        if (!found) System.out.println("Нет аппаратов, требующих обслуживания.");
        log.info("Проведено техническое обслуживание авиапарка.");
    }

    // 1-3. Добавление (разные типы)
    public void addAircraft(Aircraft a) {
        fleet.add(a);
        log.info("Добавлен аппарат: " + a.getModel());
    }

    // 4. Просмотр всех
    public void showAll() {
        if (fleet.isEmpty()) System.out.println("Аэропорт пуст.");
        else fleet.forEach(Aircraft::introduce);
    }

    // 5. Удаление (Админ)
    // Метод удаления с проверкой индекса
    public void removeAircraft(int index) {
        index--;
        if (index >= 0 && index < fleet.size()) {
            Aircraft removed = fleet.remove(index);
            log.info("Удален аппарат: " + removed.getModel() + " (индекс " + index + ")");
            System.out.println("[OK] Аппарат '" + removed.getModel() + "' успешно удален.");
        } else {
            log.warn("Попытка удаления по несуществующему индексу: " + index);
            System.out.println("(!) ОШИБКА: Аппарата с индексом " + index + " не существует.");
            System.out.println("Доступные индексы: от 0 до " + (fleet.size() - 1));
        }
    }

    // Метод редактирования с проверкой индекса
    public void editAircraft(int index, int newRange, double newPayload) {
        index--;
        if (index >= 0 && index < fleet.size()) {
            Aircraft a = fleet.get(index);
            a.setRange(newRange);
            a.setPayload(newPayload);
            log.info("Изменены данные для: " + a.getModel());
            System.out.println("[OK] Данные для '" + a.getModel() + "' обновлены.");
        } else {
            log.warn("Попытка редактирования по несуществующему индексу: " + index);
            System.out.println("(!) ОШИБКА: Не удалось найти объект с индексом " + index);
        }
    }

    // 7. Поиск по модели
    public void searchByModel(String model) {
        fleet.stream().filter(a -> a.getModel().equalsIgnoreCase(model)).forEach(Aircraft::introduce);
    }

    // 8. Фильтр по типу
    public void filterByType(Class<?> clazz) {
        fleet.stream().filter(clazz::isInstance).forEach(Aircraft::introduce);
    }

    // 9. Фильтр по году
    public void filterByYear(int year) {
        fleet.stream().filter(a -> a.getYear() == year).forEach(Aircraft::introduce);
    }

    // 10. Фильтр по диапазону лет
    public void filterByYearRange(int start, int end) {
        fleet.stream().filter(a -> a.getYear() >= start && a.getYear() <= end).forEach(Aircraft::introduce);
    }

    // 11. Сортировка по модели
    public void sortByModel() {
        fleet.sort(Comparator.comparing(Aircraft::getModel));
        System.out.println("Отсортировано по модели.");
    }

    // 12. Сортировка по дальности
    public void sortByRange() {
        fleet.sort(Comparator.comparingInt(Aircraft::getRange));
        System.out.println("Отсортировано по дальности.");
    }

    // 13. Статистика
    public void showStats() {
        if (fleet.isEmpty()) {
            System.out.println("Статистика недоступна: аэропорт пуст.");
            return;
        }

        long p = fleet.stream().filter(a -> a instanceof Plane).count();
        long h = fleet.stream().filter(a -> a instanceof Helicopter).count();
        long d = fleet.stream().filter(a -> a instanceof Drone).count();

        // Считаем среднюю грузоподъемность
        double avgPayload = fleet.stream().mapToDouble(Aircraft::getPayload).average().orElse(0.0);
        // Находим максимальную дальность
        int maxRange = fleet.stream().mapToInt(Aircraft::getRange).max().orElse(0);

        System.out.println("\n--- СТАТИСТИКА АЭРОПОРТА ---");
        System.out.println("Всего аппаратов: " + fleet.size());
        System.out.println("- Самолетов: " + p);
        System.out.println("- Вертолетов: " + h);
        System.out.println("- Дронов: " + d);
        System.out.printf("Средняя грузоподъемность: %.2f кг\n", avgPayload);
        System.out.println("Максимальная дальность в парке: " + maxRange + " км");
    }

    // 14. Сохранение
    public void saveToFile(String path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(fleet);
            log.info("Данные сохранены в " + path);
        } catch (IOException e) { log.error("Ошибка сохранения", e); }
    }

    // 15. Загрузка
    public void loadFromFile(String path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            fleet = (List<Aircraft>) ois.readObject();
            log.info("Данные загружены из " + path);
        } catch (Exception e) { log.error("Ошибка загрузки", e); }
    }
    public void exportToCSV(String filename) {
        if (fleet.isEmpty()) {
            System.out.println("База пуста, нечего экспортировать.");
            return;
        }
        // Используем обычный FileWriter для текстового файла
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(filename))) {
            writer.println("Тип;Модель;Год;Дальность;Нагрузка"); // Заголовок CSV
            for (Aircraft a : fleet) {
                writer.println(a.getClass().getSimpleName() + ";" +
                        a.getModel() + ";" +
                        a.getYear() + ";" +
                        a.getRange() + ";" +
                        a.getPayload());
            }
            log.info("Данные успешно экспортированы в CSV: " + filename);
            System.out.println("[OK] Файл " + filename + " успешно создан!");
        } catch (java.io.IOException e) {
            log.error("Ошибка при экспорте в CSV", e);
            System.out.println("(!) Ошибка записи в файл.");
        }
    }
    public void showTopRange() {
        if (fleet.isEmpty()) {
            System.out.println("Аэропорт пуст.");
            return;
        }
        System.out.println("\n--- ТОП-3 аппарата по дальности полета ---");
        fleet.stream()
                .sorted(java.util.Comparator.comparingInt(Aircraft::getRange).reversed()) // По убыванию
                .limit(3) // Берем только 3
                .forEach(a -> System.out.println(a.getModel() + " | Дальность: " + a.getRange() + " км"));
    }
    // FLYABLE ===
    public void flyAll() {
        System.out.println("\n--- Запуск демонстрационного полёта всех аппаратов (интерфейс Flyable) ---");
        boolean anyFlew = false;

        for (Aircraft a : fleet) {
            if (a instanceof Flyable) {
                ((Flyable) a).fly();
                anyFlew = true;
            }
        }

        if (!anyFlew) {
            System.out.println("В парке нет аппаратов, способных летать.");
        } else {
            System.out.println("[OK] Полёт завершён для всех летающих аппаратов.");
        }

        log.info("Выполнена демонстрация полёта через интерфейс Flyable");
    }
}