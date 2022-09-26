package salaryCheck.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class AppData {

    private static volatile AppData instance;

    private Store currentStore;

    private ObservableList<StoreTableRow> storeTable = FXCollections.observableArrayList();

    private ObservableList<Store> stores = FXCollections.observableArrayList(
            new Store("Магазин 1", 1200, 200, 0.06),
            new Store("Магазин 2", 1200, 200, 0.06),
            new Store("Магазин 3", 1200, 200, 0.06)
    );

    private ObservableList<Employee> employees = FXCollections.observableArrayList(
            new Employee("Васян"),
            new Employee("Мистер Пропер")
    );

    private ObservableList<String> expenseTypes = FXCollections.observableArrayList(
            "Зарплата",
            "Бензин МГ",
            "Хоз. нужды",
            "Другое:"
    );

    private AppData() {
        //Для начала сунем всё сюдой
        currentStore = stores.get(0);
        for(int i = 0; i < 30; i++){
            StoreTableRow storeTableRow = new StoreTableRow();
            // todo .get(0) там concurrent вылазит
            storeTableRow.addExpense(new Expense(100, expenseTypes.get(2)));
            storeTableRow.addExpense(new Expense(200, expenseTypes.get(1)));
            storeTableRow.setDate(LocalDate.now().minusDays(i));
            storeTableRow.setEmployee(employees.get(0));
            employees.get(0).addWorkDay(LocalDate.now().minusDays(i), currentStore);
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

    public Store getCurrentStore() {
        return currentStore;
    }

    public ObservableList<Store> getStores() {
        return stores;
    }

    public ObservableList<StoreTableRow> getStoreTable() {
        return storeTable;
    }
    public ObservableList<Employee> getEmployees() { return employees; }

    public ObservableList<String> getExpenseTypes() {
        return expenseTypes;
    }
}
