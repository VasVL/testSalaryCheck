package salaryCheck.model;

import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.time.LocalDate;

public class Employee {

    private String name;
    private ObservableMap<LocalDate, Store> workDays;

    // Это не буду синхронизовать
    private IntegerProperty salaryBalance;

    public Employee() {
        this("");
    }

    public Employee(String name) {
        this.name = name;
        workDays = FXCollections.observableHashMap();
    }

    public String getName() {
        return name;
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

    public int getSalaryBalance() {
        return salaryBalance.get();
    }

    public IntegerProperty salaryBalanceProperty() {
        return salaryBalance;
    }

    @Override
    public String toString() {
        return name;
    }
}
