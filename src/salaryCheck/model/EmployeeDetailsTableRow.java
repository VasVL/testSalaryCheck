package salaryCheck.model;

import javafx.beans.property.*;

import java.time.LocalDate;

public class EmployeeDetailsTableRow {

    private ObjectProperty<LocalDate> dateProperty;
    private ObjectProperty<Store> storeProperty;
    private IntegerProperty dayFeeProperty;
    private IntegerProperty dayPayProperty; // хе-хе
    private StringProperty alreadyGotPaymentProperty;
    private IntegerProperty dayPaymentBalanceProperty;

    public EmployeeDetailsTableRow() {
        this(LocalDate.now(), new Store(), 0, 0, "", 0);
    }

    public EmployeeDetailsTableRow(LocalDate date,
                                   Store store,
                                   Integer dayFee,
                                   Integer dayPay,
                                   String alreadyGotPayment,
                                   Integer daySalaryBalance) {
        this.dateProperty = new SimpleObjectProperty<>(date);
        this.storeProperty = new SimpleObjectProperty<>(store);
        this.dayFeeProperty = new SimpleIntegerProperty(dayFee);
        this.dayPayProperty = new SimpleIntegerProperty(dayPay);
        this.alreadyGotPaymentProperty = new SimpleStringProperty(alreadyGotPayment);
        this.dayPaymentBalanceProperty = new SimpleIntegerProperty(daySalaryBalance);
    }




    public LocalDate getDate() {
        return dateProperty.get();
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return dateProperty;
    }

    public void setDate(LocalDate date) {
        this.dateProperty.set(date);
    }




    public Store getStore() {
        return storeProperty.get();
    }

    public ObjectProperty<Store> storeProperty() {
        return storeProperty;
    }

    public void setStore(Store store) {
        this.storeProperty.set(store);
    }




    public int getDayFee() {
        return dayFeeProperty.get();
    }

    public IntegerProperty dayFeeProperty() {
        return dayFeeProperty;
    }

    public void setDayFee(int dayFee) {
        this.dayFeeProperty.set(dayFee);
    }




    public int getDayPay() {
        return dayPayProperty.get();
    }

    public IntegerProperty dayPayProperty() {
        return dayPayProperty;
    }

    public void setDayPay(int dayPay) {
        this.dayPayProperty.set(dayPay);
    }




    public String getAlreadyGotPayment() {
        return alreadyGotPaymentProperty.get();
    }

    public StringProperty alreadyGotPaymentProperty() {
        return alreadyGotPaymentProperty;
    }

    public void setAlreadyGotPayment(String alreadyGotPayment) {
        this.alreadyGotPaymentProperty.set(alreadyGotPayment);
    }




    public int getDaySalaryBalance() {
        return dayPaymentBalanceProperty.get();
    }

    public IntegerProperty daySalaryBalanceProperty() {
        return dayPaymentBalanceProperty;
    }

    public void setDaySalaryBalance(int dayPaymentBalance) {
        this.dayPaymentBalanceProperty.set(dayPaymentBalance);
    }


}
