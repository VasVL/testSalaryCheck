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
            StoreTableRow storeTableRow_1 = new StoreTableRow();
            StoreTableRow storeTableRow_2 = new StoreTableRow();
            StoreTableRow storeTableRow_3 = new StoreTableRow();

            storeTableRow_1.addExpense(new Expense(100, expenseTypes.get(2)));
            storeTableRow_1.addExpense(new Expense(200, expenseTypes.get(1)));
            storeTableRow_1.setDate(LocalDate.now().minusDays(i));
            storeTableRow_1.setEmployee(employees.get(0));

            storeTableRow_2.addExpense(new Expense(300, expenseTypes.get(2)));
            storeTableRow_2.addExpense(new Expense(200, expenseTypes.get(3)));
            storeTableRow_2.setDate(LocalDate.now().minusDays(i));
            storeTableRow_2.setEmployee(employees.get(1));

            storeTableRow_3.addExpense(new Expense(300, expenseTypes.get(1)));
            storeTableRow_3.setDate(LocalDate.now().minusDays(i));
            int random = (int)(Math.random()*2);
            storeTableRow_3.setEmployee(employees.get(random));

            employees.get(0).addWorkDay(LocalDate.now().minusDays(i), stores.get(0));
            employees.get(1).addWorkDay(LocalDate.now().minusDays(i), stores.get(1));
            employees.get(random).addWorkDay(LocalDate.now().minusDays(i), stores.get(2));

            stores.get(0).addStoreTableRow(storeTableRow_1);
            stores.get(1).addStoreTableRow(storeTableRow_2);
            stores.get(2).addStoreTableRow(storeTableRow_3);
            //storeTable.add(storeTableRow_1);
        }

        fillStoreTable();
    }

    public static AppData getInstance(){
        if(instance == null){
            synchronized (AppData.class){
                if(instance == null){
                    instance = new AppData();
                }
            }
        }

        return instance;
    }

    private void fillStoreTable(){

        for(Store store : stores){
            if(store.equals(currentStore)){
                storeTable.clear();
                storeTable.addAll(currentStore.getStoreTable());
                return;
            }
        }
    }

    //private void clearStoreTable(){
    //
    //}

    public Store getCurrentStore() {
        return currentStore;
    }

    public void setCurrentStore(Store currentStore) {
        this.currentStore = currentStore;
        fillStoreTable();
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
