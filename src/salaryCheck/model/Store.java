package salaryCheck.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Store {

    private String name;
    private Integer shiftPay;
    private Integer cleaningPay;
    private Double salesPercentage;

    private ObservableList<StoreTableRow> storeTable;

    public Store() {

        //this("Неизвестный магазин", 1200, 200, 0.06);
    }

    public Store(String name, Integer shiftPay, Integer cleaningPay, Double salesPercentage) {
        this.name = name;
        this.shiftPay = shiftPay;
        this.cleaningPay = cleaningPay;
        this.salesPercentage = salesPercentage;
        this.storeTable = FXCollections.observableArrayList();
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


    public ObservableList<StoreTableRow> getStoreTable() {
        return storeTable;
    }

    public void setStoreTable(ObservableList<StoreTableRow> storeTable) {
        this.storeTable = storeTable;
    }
    public void addStoreTableRow(StoreTableRow storeTableRow){
        this.storeTable.add(storeTableRow);
    }

    @Override
    public String toString() {
        return name;
    }
}
