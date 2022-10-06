package salaryCheck.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.*;
import java.time.LocalDate;

@XmlType(factoryClass = AppData.class, factoryMethod = "getInstance", propOrder = {"stores", "employees", "expenseTypes"})
@XmlRootElement
public class AppData {

    private static volatile AppData instance;

    private Store currentStore;


    private ObservableList<StoreTableRow> storeTable;

    /*
    * Следующие три листа: stores, employees, expenseTypes нужно сохранять в виде XML
    *
    * */
    private ObservableList<Store> stores;
    private ObservableList<Employee> employees;
    private ObservableList<String> expenseTypes;

    private AppData() {

        storeTable = FXCollections.observableArrayList();
        stores = FXCollections.observableArrayList();
        employees = FXCollections.observableArrayList();
        expenseTypes = FXCollections.observableArrayList(
                "Зарплата",
                "Другое:"
        );
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


    // В итоге этот метод перезаписывает везде сотрудников на тех, что есть в списке
    // todo проблема: метод отвечает за два разных действия:
    //  - берёт сотрудников, упоминаемых в таблицах магазинов и перезаписывает на таких же, но которые хранятся в списке
    //  - считает рабочие дни сотрудников
    public void setEmployeesWorkDays(){ // todo сюда же добавить подсчёт остатка по зп
        for(Employee employee : employees){
            for(Store store : stores){
                for(StoreTableRow tableRow : store.getStoreTable()){
                    for(Expense expense : tableRow.getExpenses()){
                        if(expense.getEmployee() != null && expense.getEmployee().getName().equals(employee.getName())){
                            expense.setEmployee(employee);
                        }
                    }
                    if(tableRow.getEmployee().getName().equals(employee.getName())){
                        tableRow.setEmployee(employee);
                        employee.addWorkDay(tableRow.getDate(), store);
                    }
                }
            }
        }
    }

    // добавление новых дней в начало таблиц для всех магазинов
    public void autoAddDaysInTables(){

        for(Store store : stores){
            while(!store.getStoreTable().get(0).getDate().equals(LocalDate.now())){
                StoreTableRow storeTableRow = new StoreTableRow();
                storeTableRow.setDate(store.getStoreTable().get(0).getDate().plusDays(1));
                store.getStoreTable().add(0, storeTableRow);
            }
        }
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

    public Store getCurrentStore() {
        return currentStore;
    }
    @XmlTransient
    public void setCurrentStore(Store currentStore) {
        this.currentStore = currentStore;
        fillStoreTable();
    }

    @XmlTransient
    public ObservableList<StoreTableRow> getStoreTable() {
        return storeTable;
    }

    public ObservableList<Store> getStores() {
        return stores;
    }
    @XmlElementWrapper(name = "stores")
    @XmlElement(name = "store")
    public void setStores(ObservableList<Store> stores) {
        this.stores = stores;
    }

    public ObservableList<Employee> getEmployees() { return employees; }
    @XmlElementWrapper(name = "employees")
    @XmlElement(name = "employee")
    public void setEmployees(ObservableList<Employee> employees) {
        this.employees = employees;
    }

    public ObservableList<String> getExpenseTypes() {
        return expenseTypes;
    }
    @XmlElementWrapper(name = "expenseTypes")
    @XmlElement(name = "expenseType")
    public void setExpenseTypes(ObservableList<String> expenseTypes) {

        this.expenseTypes = expenseTypes;
        if(!expenseTypes.contains("Зарплата")){
            expenseTypes.add("Зарплата");
        }
    }
}
