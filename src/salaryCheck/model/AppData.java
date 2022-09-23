package salaryCheck.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class AppData {

    private volatile static AppData instance;

    private ObservableList<StoreTableRow> storeTable = FXCollections.observableArrayList();

    private ObservableList<Employee> employees = FXCollections.observableArrayList(new Employee("Васян"), new Employee("Мистер Пропер"));

    private ObservableList<String> expensePurposes = FXCollections.observableArrayList(
            "Зарплата",
            "На бензин",
            "На телефон"
    );

    private AppData() {
        //Для начала сунем всё сюдой
        for(int i = 0; i < 30; i++){
            StoreTableRow storeTableRow = new StoreTableRow();
            storeTableRow.addExpense(new Expense(100, expensePurposes.get(0)));
            storeTableRow.addExpense(new Expense(200, expensePurposes.get(1)));
            //storeTableRow.addExpense(new Expense(300, expensePurposes.get(2)));
            //storeTableRow.addExpense(new Expense(100, "всяка дрянь"));
            storeTableRow.setDate(LocalDate.now().minusDays(i));
            storeTableRow.setEmployee(employees.get(0));
            storeTable.add(storeTableRow);
        }
    }

    public static synchronized AppData getInstance(){
        if(instance == null){
            synchronized (AppData.class){
                if(instance == null){
                    instance = new AppData();
                }
            }
        }

        return instance;
    }

    public ObservableList<StoreTableRow> getStoreTable() {
        return storeTable;
    }
    public ObservableList<Employee> getEmployees() { return employees; }

    public ObservableList<String> getExpensePurposes() {
        return expensePurposes;
    }
}
