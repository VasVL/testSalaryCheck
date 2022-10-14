package salaryCheck.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import salaryCheck.LocalDateAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

public class Expense {

    private Integer amount;
    //private String purpose;
    private StringProperty purpose;

    //private ExpenseType expenseType;
    private ObjectProperty<ExpenseType> expenseType;
    private Employee employee;
    private String store;    // Здесь переделываю Store в String из-за ошибки сериализации: возникает бесконечный цикл
    //todo возможность расчёта зп за месяц
    //private final String allSalary = "за месяц";
    private LocalDate date;
    private String comment;

    public Expense() {
        this(0, "");
    }

    public Expense(Integer amount, String type) {
        this.amount = amount;
        //this.expenseType = new ExpenseType(type);
        this.expenseType = new SimpleObjectProperty<>(new ExpenseType(type));
        //this.purpose = type;

        this.purpose = new SimpleStringProperty(type);
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }



//    public String getPurpose() {
//        return purpose;
//    }
    public String getPurpose() {
        return purpose.getValue();
    }

//    public void setPurpose(String purpose) {
//        this.purpose = purpose;
//    }
    public void setPurpose(String purpose) {
        this.purpose.setValue(purpose);
    }

    public StringProperty purposeProperty(){
        return purpose;
    }


    //public ExpenseType getExpenseType() {
    //    return expenseType;
    //}
    public ExpenseType getExpenseType() {
        return expenseType.getValue();
    }

    //public void setExpenseType(ExpenseType expenseType) {
    //    this.expenseType = expenseType;
    //}
    public void setExpenseType(ExpenseType expenseType) {
        this.expenseType.setValue(expenseType);
        //this.purpose.setValue(expenseType.getName() + " " + comment); // todo разные типы expense
    }

    public ObjectProperty<ExpenseType> expenseTypeProperty(){
        return expenseType;
    }



    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }



    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
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



    @Override
    public String toString() {
        // todo purpose.stream().
        if(expenseType.getValue().getName().equals("Зарплата")){
            purpose.setValue(expenseType.getValue().getName() + " " + employee.getName() + " " + store + " " + date + " " + comment);
        } else {
            purpose.setValue(expenseType.getValue().getName() + " " + comment);
        }
        return amount + " - " + purpose.getValue();
    }
}
