package salaryCheck.model;

public class Expense {

    private Integer amount;
    private String purpose;

    public Expense() {
        amount = Integer.valueOf(0);
        purpose = "";
    }

    public Expense(Integer amount, String purpose) {
        this.amount = amount;
        this.purpose = purpose;
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

    @Override
    public String toString() {
        return amount + " - " + purpose;
    }
}
