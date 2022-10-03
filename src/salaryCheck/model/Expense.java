package salaryCheck.model;

import salaryCheck.LocalDateAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

public class Expense {

    private Integer amount;
    private String purpose;

    private String expenseType;
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
        this.expenseType = type;
        this.purpose = type;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }



    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }



    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
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
        return amount + " - " + purpose;
    }
}
