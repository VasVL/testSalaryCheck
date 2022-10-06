package salaryCheck.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.time.LocalDate;

public class Store {

    private String name;
    private Integer shiftPay;
    private Integer cleaningPay;
    private Double salesPercentage;
    private boolean isActive;


    private ObservableList<StoreTableRow> storeTable;

    public Store() {

        //this("Неизвестный магазин", 1200, 200, 0.06);
        this.storeTable = FXCollections.observableArrayList();
        // todo здесь оставить добавлениетолько одной строки, и сделать кнопку для добавления сткрок
        for(int i = 0; i < 30; i++){
            StoreTableRow storeTableRow = new StoreTableRow();
            storeTableRow.setDate(LocalDate.now().minusDays(i));
            this.addStoreTableRow(storeTableRow);
        }
        this.isActive = true;
    }

    public Store(String name, Integer shiftPay, Integer cleaningPay, Double salesPercentage) {
        this();
        this.name = name;
        this.shiftPay = shiftPay;
        this.cleaningPay = cleaningPay;
        this.salesPercentage = salesPercentage;
        //this.storeTable = FXCollections.observableArrayList();
    }

    public String getName() {
        return name;
    }

    @XmlElement(name = "name")
    public void setName(String name) {
        this.name = name;
    }



    public Integer getShiftPay() {
        return shiftPay;
    }

    @XmlElement(name = "shiftPay")
    public void setShiftPay(Integer shiftPay) {
        this.shiftPay = shiftPay;
    }



    public Integer getCleaningPay() {
        return cleaningPay;
    }

    @XmlElement(name = "cleaningPay")
    public void setCleaningPay(Integer cleaningPay) {
        this.cleaningPay = cleaningPay;
    }



    public Double getSalesPercentage() {
        return salesPercentage;
    }

    @XmlElement(name = "salesPercentage")
    public void setSalesPercentage(Double salesPercentage) {
        this.salesPercentage = salesPercentage;
    }

    public boolean isActive() {
        return isActive;
    }
    @XmlElement(name = "isActive")
    public void setActive(boolean active) {
        isActive = active;
    }

    public ObservableList<StoreTableRow> getStoreTable() {
        return storeTable;
    }

    @XmlElementWrapper(name = "storeTable")
    @XmlElement(name = "storeTableRow")
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
