package salaryCheck.model;

import javafx.beans.property.IntegerProperty;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Employee {

    private String name;
    private Map<LocalDate, Store> workDays;

    // Это не буду синхронизовать
    private IntegerProperty salaryBalance;

    public Employee(String name) {
        this.name = name;
        workDays = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
