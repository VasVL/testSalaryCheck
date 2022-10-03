package salaryCheck.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import salaryCheck.LocalDateAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.Map;

public class Employee {

    private String name;
    // todo  подумать, нужно ли хранить это или можно рассчитывать каждый раз (пока хочу рассчитывать после инициализации XML-документа)

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

    @XmlElement(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public ObservableMap<LocalDate, Store> getWorkDays() {
        return workDays;
    }
    @XmlTransient
    public void setWorkDays(ObservableMap<LocalDate, Store> workDays) {
        this.workDays = workDays;
    }

    public void addWorkDay(LocalDate date, Store store){
        workDays.put(date, store);
    }

    public void removeWorkDay(LocalDate date){
        workDays.remove(date);
    }

    @XmlTransient
    public Integer getSalaryBalance() {

        salaryBalance = 0;
        for(Map.Entry<LocalDate, Store> entry : workDays.entrySet()){
            Store store = entry.getValue();
            StoreTableRow tableRow = store.getStoreTable().stream().filter(row -> row.getDate().equals(entry.getKey())).findAny().orElse(null);

            if (tableRow != null) {
                salaryBalance += store.getShiftPay() +
                        store.getCleaningPay() +
                        (int)(store.getSalesPercentage() * tableRow.getAllFee());

                // todo тут неверно: смотрит расходы только в свои рабочие дни
                for(Expense expense : tableRow.getExpenses()){
                    if(expense.getExpenseType().equals("Зарплата") && expense.getEmployee().equals(this)){
                        salaryBalance -= expense.getAmount();
                    }
                }
            }
        }
        return salaryBalance;
    }

    public void addSalaryBalance(int amount){
        this.salaryBalance += amount;
    }

    @Override
    public String toString() {
        return name;
    }
}
