package com.example.airport;

import java.util.Scanner;
import org.apache.log4j.PropertyConfigurator;
import static com.example.airport.AirportManager.log;

public class Main {
    // Метод для безопасного ввода целых чисел
    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                log.error("Некорректный ввод числа. Ожидалось int, получено: '" + input + "'");
                System.out.println("(!) Ошибка: Пожалуйста, введите целое число.");
            }
        }
    }

    // Метод для безопасного ввода дробных чисел
    private static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().replace(',', '.');
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                log.error("Некорректный ввод дробного числа. Получено: '" + input + "'");
                System.out.println("(!) Ошибка: Введите число (через точку или запятую).");
            }
        }
    }

    public static void main(String[] args) {
        PropertyConfigurator.configure(Main.class.getClassLoader().getResource("log4j.properties"));

        AirportManager manager = new AirportManager();
        AuthService authService = new AuthService();
        Scanner sc = new Scanner(System.in);
        boolean systemRunning = true;

        System.out.println("=== Система управления Аэропортом ===");

        // --- БЛОК ВХОДА И РЕГИСТРАЦИИ ---
        while (systemRunning) {
            User currentUser = null;
            while (currentUser == null) {
                System.out.println("\n[1] Войти в систему");
                System.out.println("[2] Зарегистрироваться");
                System.out.println("[0] Полный выход");
                System.out.print("Выберите действие: ");

                int authChoice = sc.nextInt();
                sc.nextLine(); // Очистка буфера

                if (authChoice == 1) {
                    System.out.print("Введите логин: ");
                    String login = sc.nextLine();
                    System.out.print("Введите пароль: ");
                    String pass = sc.nextLine();

                    currentUser = authService.login(login, pass);
                    if (currentUser == null) {
                        System.out.println("(!) Ошибка: Неверный логин или пароль.");
                    }
                } else if (authChoice == 2) {
                    System.out.print("Придумайте логин: ");
                    String login = sc.nextLine();
                    System.out.print("Придумайте пароль: ");
                    String pass = sc.nextLine();
                    authService.register(login, pass);
                } else if (authChoice == 0) {
                    systemRunning = false;
                    break;
                }
            }
            if (!systemRunning) break;

            System.out.println("\nУспешный вход! Вы зашли как: " + currentUser.getLogin() + " [" + currentUser.getRole() + "]");

            // --- ГЛАВНОЕ МЕНЮ ПРОГРАММЫ ---
            boolean running = true;
            while (running) {
                boolean isAdmin = currentUser.getRole().equals("ADMIN");

                System.out.println("\n========= ГЛАВНОЕ МЕНЮ =========");
                // Общие пункты (доступны всем)
                System.out.println("1. Просмотреть все аппараты");
                System.out.println("2. Поиск по модели");
                System.out.println("3. Фильтр по типу");
                System.out.println("4. Фильтр по году");
                System.out.println("5. Фильтр по диапазону лет");
                System.out.println("6. Показать статистику");
                System.out.println("7. Топ-3 аппарата по дальности (ЛР №3)");

                // Пункты только для Админа
                if (isAdmin) {
                    System.out.println("--- АДМИНИСТРИРОВАНИЕ ---");
                    System.out.println("8. Добавить самолет");
                    System.out.println("9. Добавить вертолет");
                    System.out.println("10. Добавить дрон");
                    System.out.println("11. Удалить аппарат (по индексу)");
                    System.out.println("12. Редактировать данные");
                    System.out.println("13. Сортировать по модели");
                    System.out.println("14. Сортировать по дальности");
                    System.out.println("15. Сохранить базу в файл (.dat)");
                    System.out.println("16. Загрузить базу из файла (.dat)");
                    System.out.println("17. Экспорт базы в CSV (ЛР №3)");
                    System.out.println("18. Выполнить тех. обслуживание (ЛР №3)");
                    System.out.println("19. Демонстрация полёта всех аппаратов (Flyable)");
                }
                System.out.println("0. Сменить пользователя / Разлогиниться");

                int choice = readInt(sc, "\nВаш выбор: ");

                // Логика прав доступа: Пункты >= 8 только для админа
                if (!isAdmin && choice >= 8 && choice <= 18) {
                    System.out.println("(!) ОТКАЗАНО В ДОСТУПЕ: У вас недостаточно прав для этого действия.");
                    continue;
                }

                switch (choice) {
                    case 1: manager.showAll(); break;
                    case 2:
                        System.out.print("Введите название модели: ");
                        manager.searchByModel(sc.nextLine());
                        break;
                    case 3:
                        int t = readInt(sc, "Тип: 1-Самолет, 2-Вертолет, 3-Дрон: ");
                        if (t == 1) manager.filterByType(Plane.class);
                        else if (t == 2) manager.filterByType(Helicopter.class);
                        else if (t == 3) manager.filterByType(Drone.class);
                        else System.out.println("Неверный тип.");
                        break;
                    case 4:
                        manager.filterByYear(readInt(sc, "Введите год: "));
                        break;
                    case 5:
                        int start = readInt(sc, "С года: ");
                        int end = readInt(sc, "По год: ");
                        manager.filterByYearRange(start, end);
                        break;
                    case 6: manager.showStats(); break;
                    case 7: manager.showTopRange(); break; // Требование ЛР 3

                    // --- БЛОК АДМИНА ---
                    case 8:
                        System.out.print("Модель: ");
                        String m1 = sc.nextLine();
                        int y1 = readInt(sc,"Год выпуска: ");
                        while(y1 < 1903 || y1 > 2026 ){
                            log.warn("Пользователь ввел недопустимый год: " + y1);
                            System.out.println("(!) Невозможный год (должен быть от 1903 до 2026).");
                            y1 = readInt(sc, "Введите ещё раз: ");
                        }
                        int r1 = readInt(sc, "Дальность полета: ");
                        double p1 = readDouble(sc, "Грузоподъемность: ");
                        double w1 = readDouble(sc, "Размах крыльев: ");
                        manager.addAircraft(new Plane(m1, y1, r1, p1, w1));
                        break;
                    case 9:
                        System.out.print("Модель: ");
                        String m2 = sc.nextLine();
                        int y2 = readInt(sc,"Год выпуска: ");
                        while(y2 < 1903 || y2 > 2026 ){
                            log.warn("Пользователь ввел недопустимый год: " + y2);
                            System.out.println("(!) Невозможный год (должен быть от 1903 до 2026).");
                            y2 = readInt(sc, "Введите ещё раз: ");
                        }
                        int r2 = readInt(sc, "Дальность полета: ");
                        double p2 = readDouble(sc, "Грузоподъемность: ");
                        double w2 = readDouble(sc, "Диаметр винта: ");
                        manager.addAircraft(new Helicopter(m2, y2, r2, p2, w2));
                        break;
                    case 10:
                        System.out.print("Модель: ");
                        String m3 = sc.nextLine();
                        int y3 = readInt(sc,"Год выпуска: ");
                        while(y3 < 1903 || y3 > 2026 ){
                            log.warn("Пользователь ввел недопустимый год: " + y3);
                            System.out.println("(!) Невозможный год (должен быть от 1903 до 2026).");
                            y3 = readInt(sc, "Введите ещё раз: ");
                        }
                        int r3 = readInt(sc, "Дальность полета: ");
                        double p3 = readDouble(sc, "Грузоподъемность: ");
                        int w3 = readInt(sc,"Батарея: ");
                        manager.addAircraft(new Drone(m3, y3, r3, p3, w3));
                        break;
                    case 11:
                        int delIdx = readInt(sc, "Индекс для удаления: ");
                        manager.removeAircraft(delIdx);
                        break;
                    case 12:
                        int edIdx = readInt(sc, "Индекс для правки: ");
                        int nr = readInt(sc, "Новая дальность: ");
                        double np = readDouble(sc, "Новая нагрузка: ");
                        manager.editAircraft(edIdx, nr, np);
                        break;
                    case 13: manager.sortByModel(); break;
                    case 14: manager.sortByRange(); break;
                    case 15: manager.saveToFile("fleet.dat"); break;
                    case 16: manager.loadFromFile("fleet.dat"); break;
                    case 17: manager.exportToCSV("fleet.csv"); break;
                    case 18: manager.performMaintenance(); break;
                    case 19: manager.flyAll(); break;
                    case 0:
                        System.out.println("Выход из аккаунта...");
                        running = false;
                        break;
                    default:
                        System.out.println("Неверный пункт меню.");
                }
            }
        }
        System.out.println("Программа полностью завершена.");
    }
}