package com.example.airport;
import java.io.*;
import java.util.*;

public class AuthService {
    private List<User> users = new ArrayList<>();
    private final String FILE_NAME = "users.dat";

    public AuthService() {
        loadUsers();
        // Если файл пустой, создаем админа по умолчанию
        if (users.isEmpty()) {
            users.add(new User("admin", "admin", "ADMIN"));
            saveUsers();
        }
    }

    public void register(String login, String pass) {
        users.add(new User(login, pass, "USER"));
        saveUsers();
        System.out.println("Регистрация успешна!");
    }

    public User login(String login, String pass) {
        return users.stream()
                .filter(u -> u.getLogin().equals(login) && u.getPassword().equals(pass))
                .findFirst()
                .orElse(null);
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(users);
        } catch (IOException e) { System.out.println("Ошибка сохранения юзеров"); }
    }

    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            users = (List<User>) ois.readObject();
        } catch (Exception e) { users = new ArrayList<>(); }
    }
}