package salaryCheck.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class AppData {

    private volatile static AppData instance;

    private ObservableList<StoreTableRow> storeTable = FXCollections.observableArrayList();
    private ObservableList<Employee> employees = FXCollections.observableArrayList(new Employee("Васян"), new Employee("Мистер Пропер"));

    private AppData() {
        //Для начала сунем всё сюдой
        for(int i = 0; i < 30; i++){
            StoreTableRow storeTableRow = new StoreTableRow();
            storeTableRow.addExpense(new Expense(100, "хоз нужды"));
            storeTableRow.addExpense(new Expense(200, "всяка дрянь"));
            storeTableRow.addExpense(new Expense(300, "др хоз нужды"));
            //storeTableRow.addExpense(new Expense(100, "всяка дрянь"));
            storeTableRow.setDate(LocalDate.now().minusDays(i));
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
}
