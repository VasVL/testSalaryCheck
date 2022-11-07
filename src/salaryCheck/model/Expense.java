package salaryCheck.model;

import javafx.beans.property.*;
import salaryCheck.LocalDateAdapter;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Expense {

    private Integer amount;
    private StringProperty purpose;
    private ObjectProperty<ExpenseType> expenseType;
    private ObjectProperty<Employee> employeeProperty;
    //private String store;    // Здесь переделываю Store в String из-за ошибки сериализации: возникает бесконечный цикл
    private String storeName;
    private ObjectProperty<Store> store;
    //todo возможность расчёта зп за месяц
    //private final String allSalary = "за месяц";
    private LocalDate date;
    private String comment;
    private BooleanProperty isCorrectProperty;

    public Expense() {
        this(0, "");
    }

    public Expense(Integer amount, String type) {
        this.amount = amount;
        //this.expenseType = new ExpenseType(type);
        this.expenseType = new SimpleObjectProperty<>(new ExpenseType(type));
        this.employeeProperty = new SimpleObjectProperty<>();
        //this.purpose = type;
        this.store = new SimpleObjectProperty<>();
        this.purpose = new SimpleStringProperty(type);
        this.isCorrectProperty = new SimpleBooleanProperty(true);
    }

    public Integer getAmount() {
        return amount;
    }
    public void setAmount(Integer amount) {
        this.amount = amount;
    }



    public String getPurpose() {
        return purpose.getValue();
    }
    public void setPurpose(String purpose) {
        this.purpose.setValue(purpose);
    }
    public StringProperty purposeProperty(){
        return purpose;
    }



    public ExpenseType getExpenseType() {
        return expenseType.getValue();
    }
    public void setExpenseType(ExpenseType expenseType) {
        this.expenseType.setValue(expenseType);
        //this.purpose.setValue(expenseType.getName() + " " + comment); // todo разные типы expense
    }
    public ObjectProperty<ExpenseType> expenseTypeProperty(){
        return expenseType;
    }



    public Employee getEmployee() {
        return employeeProperty.getValue();
    }
    public ObjectProperty<Employee> employeeProperty(){
        return employeeProperty;
    }
    public void setEmployee(Employee employeeProperty) {
        this.employeeProperty.setValue(employeeProperty);
    }



    public String getStoreName() {
        return storeName;
    }
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }



    public Store getStore() {
        return store.getValue();
    }
    @XmlTransient
    public void setStore(Store store) {
        this.store.setValue(store);
        this.storeName = store.getName();
    }
    public ObjectProperty<Store> storeProperty(){
        return store;
    }



    public LocalDate getDate() {
        return date;
    }
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public void setDate(LocalDate date) {
        this.date = date;
    }



    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public boolean isCorrect() {
        return isCorrectProperty.get();
    }
    public BooleanProperty isCorrectProperty() {
        return isCorrectProperty;
    }
    public void setIsCorrect(boolean isCorrect) {
        this.isCorrectProperty.set(isCorrect);
    }

    @Override
    public String toString() {
        // todo purpose.stream().
        if(expenseType.getValue().getName().equals("Зарплата")){
            purpose.setValue(expenseType.getValue().getName() + "  " + employeeProperty.getValue() + "  " + store.getValue().getName()  + "  за  " + date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("ru"))) + " " + comment);
        } else {
            purpose.setValue(expenseType.getValue().getName() + " " + comment);
        }
        return amount + "  -  " + purpose.getValue();
    }
}
