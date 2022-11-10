package salaryCheck.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.xml.bind.annotation.XmlElement;

public class ExpenseType {

    private StringProperty name;
    private BooleanProperty isActive;

    public ExpenseType() {
        this("");
    }

    public ExpenseType(String name) {

        this.name = new SimpleStringProperty(name);
        this.isActive = new SimpleBooleanProperty(true);
    }

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public StringProperty nameProperty(){
        return name;
    }



    @XmlElement(name = "isActive")
    public boolean getActive() {
        return isActive.get();
    }

    public BooleanProperty isActiveProperty() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive.set(isActive);
    }



    @Override
    public String toString() {
        return name.getValue();
    }
}
