package salaryCheck.model;

import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.time.LocalDate;

public class Employee {

    private String name;
    private ObservableMap<LocalDate, Store> workDays;

    // Это не буду синхронизовать
    private Integer salaryBalance;

    public Employee() {
        this("");
    }

    public Employee(String name) {
        this.name = name;
        workDays = FXCollections.observableHashMap();
        salaryBalance = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObservableMap<LocalDate, Store> getWorkDays() {
        return workDays;
    }

    public void addWorkDay(LocalDate date, Store store){
        workDays.put(date, store);
    }

    public void removeWorkDay(LocalDate date){
        workDays.remove(date);
    }

    public Integer getSalaryBalance() {
        return salaryBalance;
    }

    @Override
    public String toString() {
        return name;
    }
}
