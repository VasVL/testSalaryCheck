package salaryCheck.model;

public class Store {

    private String name;
    private Integer shiftPay;
    private Integer cleaningPay;
    private Double salesPercentage;

    public Store() {

        //this("Неизвестный магазин", 1200, 200, 0.06);
    }

    public Store(String name, Integer shiftPay, Integer cleaningPay, Double salesPercentage) {
        this.name = name;
        this.shiftPay = shiftPay;
        this.cleaningPay = cleaningPay;
        this.salesPercentage = salesPercentage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getShiftPay() {
        return shiftPay;
    }

    public void setShiftPay(Integer shiftPay) {
        this.shiftPay = shiftPay;
    }

    public Integer getCleaningPay() {
        return cleaningPay;
    }

    public void setCleaningPay(Integer cleaningPay) {
        this.cleaningPay = cleaningPay;
    }

    public Double getSalesPercentage() {
        return salesPercentage;
    }

    public void setSalesPercentage(Double salesPercentage) {
        this.salesPercentage = salesPercentage;
    }

    @Override
    public String toString() {
        return name;
    }
}
