package salaryCheck.model;

public class Expense {

    private Integer amount;
    private String purpose;

    public Expense() {
    }

    public Expense(Integer amount, String purpose) {
        this.amount = amount;
        this.purpose = purpose;
    }

    @Override
    public String toString() {
        return amount + " - " + purpose;
    }
}
