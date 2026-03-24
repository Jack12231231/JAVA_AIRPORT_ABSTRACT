package com.example.airport;
import java.io.*;

// Базовый класс
abstract class Aircraft implements Serializable {
    private String model;
    private int year;
    private int range;
    private double payload;
    private boolean needsMaintenance = true;

    public Aircraft(String model, int year, int range, double payload) {
        this.model = model;
        this.year = year;
        this.range = range;
        this.payload = payload;
        this.needsMaintenance = true;
    }

    // Геттеры и сеттеры (Инкапсуляция)
    public String getModel() { return model; }
    public int getYear() { return year; }
    public int getRange() { return range; }
    public void setRange(int range) { this.range = range; }
    public double getPayload() { return payload; }
    public void setPayload(double payload) { this.payload = payload; }

    // Новые методы для проверки ТО
    public boolean needsMaintenance() { return needsMaintenance; }
    protected void markAsMaintained() { this.needsMaintenance = false; }

    public abstract void introduce(); // Полиморфный метод
}

// Наследник 1: Самолет
class Plane extends Aircraft implements Rentable, Maintainable {
    private double wingSpan;

    public Plane(String model, int year, int range, double payload, double wingSpan) {
        super(model, year, range, payload);
        this.wingSpan = wingSpan;
    }

    @Override
    public void rent(int days) {
        System.out.println("Самолет " + getModel() + " арендован на " + days + " дней (чартерный рейс).");
    }

    @Override
    public void performMaintenance() {
        System.out.println("[ТО] Проверка турбин и шасси для самолета: " + getModel());
        markAsMaintained();
    }

    @Override
    public void introduce() {
        System.out.printf("Самолет: %s, Год: %d, Дальность: %d, Грузоподъемность: %.1f, Размах крыльев: %.1f\n",
                getModel(), getYear(), getRange(), getPayload(), wingSpan);
    }
}

// Наследник 2: Вертолет
class Helicopter extends Aircraft implements Rentable, Maintainable {
    private double rotorDiameter;

    public Helicopter(String model, int year, int range, double payload, double rotorDiameter) {
        super(model, year, range, payload);
        this.rotorDiameter = rotorDiameter;
    }

    @Override
    public void rent(int days) {
        System.out.println("Вертолет " + getModel() + " арендован на " + days + " дней (для экскурсий).");
    }

    @Override
    public void performMaintenance() {
        System.out.println("[ТО] Смазка несущего винта для вертолета: " + getModel());
        markAsMaintained();
    }

    @Override
    public void introduce() {
        System.out.printf("Вертолет: %s, Год: %d, Дальность: %d, Грузоподъемность: %.1f, Диаметр винта: %.1f\n",
                getModel(), getYear(), getRange(), getPayload(), rotorDiameter);
    }
}

// Наследник 3: Дрон
class Drone extends Aircraft implements Rentable {
    private int batteryCapacity;

    public Drone(String model, int year, int range, double payload, int batteryCapacity) {
        super(model, year, range, payload);
        this.batteryCapacity = batteryCapacity;
    }

    @Override
    public void rent(int days) {
        System.out.println("Дрон " + getModel() + " арендован на " + days + " дней (для съёмки).");
    }

    @Override
    public void introduce() {
        System.out.printf("Дрон: %s, Год: %d, Дальность: %d, Грузоподъемность: %.1f, Батарея: %d мАч\n",
                getModel(), getYear(), getRange(), getPayload(), batteryCapacity);
    }
}