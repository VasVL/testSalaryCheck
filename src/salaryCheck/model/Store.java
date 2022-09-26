package salaryCheck.model;

public class Store {

    private String name;
    private Integer shiftPay;
    private Integer cleaningPay;
    private Double salesPercentage;

    public Store() {
        this("Неизвестный магазин", 1200, 200, 0.06);
    }

    public Store(String name, Integer shiftPay, Integer cleaningPay, Double salesPercentage) {
        this.name = name;
        this.shiftPay = shiftPay;
        this.cleaningPay = cleaningPay;
        this.salesPercentage = salesPercentage;
    }

    @Override
    public String toString() {
        return name;
    }
}
