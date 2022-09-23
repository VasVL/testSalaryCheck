package salaryCheck.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;

/**
 * Класс для записи значений в таблицу
 */
public class StoreTableRow {

    private ObjectProperty<LocalDate> date;
    private ObjectProperty<Employee> employee;
    private IntegerProperty allFee;
    private IntegerProperty nonCash;
    private IntegerProperty cash;
    private IntegerProperty cashBalance;
    //private ObjectProperty<ObservableList<Expense>> expenses;
    private ListProperty<Expense> expenses;

    /**
    * Конструктор по умолчанию.
     */
    public StoreTableRow() {
        this(LocalDate.now(),
                new Employee(""),
                0, 0, 0, 0,
                FXCollections.observableArrayList());
    }

    /**
     *
     * @param date
     * @param employee
     * @param allFee
     * @param nonCash
     * @param cash
     * @param cashBalance
     * @param expenses
     */
    public StoreTableRow(LocalDate date,
                         Employee employee,
                         Integer allFee,
                         Integer nonCash,
                         Integer cash,
                         Integer cashBalance,
                         ObservableList<Expense> expenses) {
        this.date = new SimpleObjectProperty<>(date);
        this.employee = new SimpleObjectProperty<>(employee);
        this.allFee = new SimpleIntegerProperty(allFee);
        this.nonCash = new SimpleIntegerProperty(nonCash);
        this.cash = new SimpleIntegerProperty(cash);
        this.cashBalance = new SimpleIntegerProperty(cashBalance);
        //this.expenses = new SimpleObjectProperty<>(expenses);
        this.expenses = new SimpleListProperty<>(expenses);
    }




    public LocalDate getDate() {
        return date.get();
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }




    public Employee getEmployee() {
        return employee.get();
    }

    public ObjectProperty<Employee> employeeProperty() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee.set(employee);
    }




    public int getAllFee() {
        return allFee.get();
    }

    public IntegerProperty allFeeProperty() {
        return allFee;
    }

    public void setAllFee(int allFee) {
        this.allFee.set(allFee);
    }




    public int getNonCash() {
        return nonCash.get();
    }

    public IntegerProperty nonCashProperty() {
        return nonCash;
    }

    public void setNonCash(int nonCash) {
        this.nonCash.set(nonCash);
    }




    public int getCash() {
        return cash.get();
    }

    public IntegerProperty cashProperty() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash.set(cash);
    }




    public int getCashBalance() {
        return cashBalance.get();
    }

    public IntegerProperty cashBalanceProperty() {
        return cashBalance;
    }

    public void setCashBalance(int cashBalance) {
        this.cashBalance.set(cashBalance);
    }




    public ObservableList<Expense> getExpenses() {
        return expenses.get();
    }

    public ListProperty<Expense> expensesProperty() {
        return expenses;
    }

    public void setExpenses(ObservableList<Expense> expenses) {
        this.expenses.set(expenses);
    }

    public void addExpense(Expense expense){
        this.getExpenses().add(expense);
    }

    public void removeExpense(Expense expense){ this.getExpenses().remove(expense); }




    public void clearRow(List<StoreTableRow> storeTableRowList, int rowNumber){
        setEmployee(new Employee(""));
        setAllFee(0);
        setNonCash(0);
        setCash(0);
        ObservableList<Expense> emptyExpensesList = FXCollections.observableArrayList();
        storeTableRowList.get(rowNumber).setExpenses(emptyExpensesList);
        setCashBalance(0);
    }
}
