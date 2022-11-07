package salaryCheck.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.time.LocalDate;

public class Employee {

    private StringProperty name;
    // todo подумать, нужно ли хранить это или можно рассчитывать каждый раз (пока хочу рассчитывать после инициализации XML-документа)
    private ObservableMap<LocalDate, Store> workDays;

    private Integer salaryBalance;

    private BooleanProperty isActive;

    public Employee() {
        this("");
    }

    public Employee(String name) {
        this.name = new SimpleStringProperty(name);
        this.workDays = FXCollections.observableHashMap();
        this.salaryBalance = 0;
        this.isActive = new SimpleBooleanProperty(true);
    }



    public String getName() {
        return name.getValue();
    }

    @XmlElement(name = "name")
    public void setName(String name) {
        this.name.setValue(name);
    }

    public StringProperty nameProperty(){
        return this.name;
    }




    @XmlElement(name = "isActive")
    public boolean getActive() {
        return isActive.get();
    }

    public BooleanProperty isActiveProperty() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive.set(isActive);
    }




    @XmlTransient
    public void setWorkDays(ObservableMap<LocalDate, Store> workDays) {
        this.workDays = workDays;
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




    @XmlTransient
    public Integer getSalaryBalance() {
        return salaryBalance;
    }

    public void setSalaryBalance(Integer salaryBalance) {
        this.salaryBalance = salaryBalance;
    }

    public void addSalaryBalance(int amount){
        this.salaryBalance += amount;
    }




    @Override
    public String toString() {
        return name.getValue();
    }
}
