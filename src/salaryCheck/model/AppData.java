package salaryCheck.model;

import javafx.beans.Observable;
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
    private ObservableList<ExpenseType> expenseTypes;

    private AppData() {

        storeTable = FXCollections.observableArrayList();
        stores = FXCollections.observableArrayList(store -> new Observable[]{store.isActiveProperty()});
        employees = FXCollections.observableArrayList(employee -> new Observable[]{employee.isActiveProperty()});
        expenseTypes = FXCollections.observableArrayList(expenseType -> new Observable[]{expenseType.isActiveProperty()});
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


    public void fillStoreTables(){ // todo сюда же добавить подсчёт остатка по зп

        if(!isExpenseTypesContains("Зарплата")){
            expenseTypes.add(0, new ExpenseType("Зарплата"));
        }

        for(Store store : stores){
            for(StoreTableRow tableRow : store.getStoreTable()){
                for(Employee employee : employees){
                    for(Expense expense : tableRow.getExpenses()){
                        for(ExpenseType expenseType : expenseTypes){
                            if( expense.getExpenseType().getName().equals(expenseType.getName()) ){
                                expense.setExpenseType(expenseType);
                            }
                        }
                        if(expense.getEmployee() != null && expense.getEmployee().getName().equals(employee.getName())){
                            expense.setEmployee(employee);
                            employee.addSalaryBalance(-expense.getAmount());
                        }
                    }
                    if( tableRow.getEmployee().getName().equals(employee.getName()) ){
                        tableRow.setEmployee(employee);
                        employee.addWorkDay(tableRow.getDate(), store);
                        employee.addSalaryBalance(
                                store.getShiftPay() +
                                store.getCleaningPay() +
                                (int)(store.getSalesPercentage() * tableRow.getAllFee()) );
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

    public ObservableList<ExpenseType> getExpenseTypes() {
        return expenseTypes;
    }
    @XmlElementWrapper(name = "expenseTypes")
    @XmlElement(name = "expenseType")
    public void setExpenseTypes(ObservableList<ExpenseType> expenseTypes) {

        this.expenseTypes = expenseTypes;
    }

    private boolean isExpenseTypesContains(String str){

        for(ExpenseType expenseType : expenseTypes){
            if(expenseType.getName().equals( str )){
                return true;
            }
        }

        return false;
    }
}
