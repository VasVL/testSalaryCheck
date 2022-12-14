package salaryCheck.model;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.*;
import java.io.File;
import java.time.LocalDate;

@XmlType(factoryClass = AppData.class, factoryMethod = "getInstance", propOrder = {"stores", "employees", "expenseTypes"})
@XmlRootElement
public class AppData {

    private Boolean wasDataChanged;

    private static volatile AppData instance;

    private final File appDataPath = new File("AppData.xml"); // В main'е то же самое

    private Store currentStore;

    private final ObservableList<StoreTableRow> storeTable;

    /*
    * Следующие три листа: stores, employees, expenseTypes нужно сохранять в виде XML
    *
    * */
    private ObservableList<Store> stores;
    private ObservableList<Employee> employees;
    private ObservableList<ExpenseType> expenseTypes;

    private AppData() {

        wasDataChanged = false;

        storeTable = FXCollections.observableArrayList(storeTableRow -> new Observable[]{
                storeTableRow.employeeProperty(),
                storeTableRow.allFeeProperty(),
                storeTableRow.nonCashProperty(),
                storeTableRow.cashProperty(),
                //storeTableRow.expensesProperty(),
                storeTableRow.getExpenses(),
                storeTableRow.cashBalanceProperty(),
                storeTableRow.isActiveProperty()
        });
        stores = FXCollections.observableArrayList(store -> new Observable[]{store.isActiveProperty(), store.getStoreTable(), store.nameProperty()});
        employees = FXCollections.observableArrayList(employee -> new Observable[]{employee.isActiveProperty(), employee.nameProperty()});
        expenseTypes = FXCollections.observableArrayList(expenseType -> new Observable[]{expenseType.isActiveProperty(), expenseType.nameProperty()});

        stores.addListener(new ListChangeListener<Store>() {
            @Override
            public void onChanged(Change<? extends Store> change) {
                while(change.next()){
                    if(change.wasUpdated() || change.wasRemoved()){
                        for(Employee employee : employees) {
                            employee.setSalaryBalance(0);
                            employee.getWorkDays().clear();
                            for (Store store : stores) {
                                for (StoreTableRow storeTableRow : store.getStoreTable()) {
                                    for(Expense expense : storeTableRow.getExpenses()){
                                        if(expense.getExpenseType().getName().equals("Зарплата")){
                                            if( expense.getEmployee().getName().equals(employee.getName())) {
                                                employee.addSalaryBalance(-expense.getAmount());
                                            }
                                        }

                                    }
                                    if(storeTableRow.getEmployee().getName().equals(employee.getName())){
                                        employee.addWorkDay(storeTableRow.getDate(), store);
                                        employee.addSalaryBalance(
                                                store.getShiftPay() +
                                                        store.getCleaningPay() +
                                                        (int)(store.getSalesPercentage() * storeTableRow.getAllFee()) );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
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


    public void updateStoreTables(){

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
                        if(expense.getExpenseType().getName().equals("Зарплата")){
                            if(expense.getEmployee().getName().equals(employee.getName())) {
                                expense.setEmployee(employee);
                            }
                                expense.setStore(
                                        stores.stream().filter(store1 -> store1.getName().equals(expense.getStoreName())).findAny().orElse(null)
                                );
                        }
                    }
                    if( tableRow.getEmployee().getName().equals(employee.getName()) ){
                        tableRow.setEmployee(employee);
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

    public void fillStoreTable(){

        for(Store store : stores){
            if(store.equals(currentStore)){
                //storeTable.clear();
                storeTable.setAll(currentStore.getStoreTable());
                //storeTable.addAll(currentStore.getStoreTable());
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



    public Boolean wasDataChanged() {
        return wasDataChanged;
    }
    @XmlTransient
    public void setWasDataChanged(Boolean wasDataChanged) {
        this.wasDataChanged = wasDataChanged;
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



    @XmlTransient
    public File getAppDataPath() {
        return appDataPath;
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
