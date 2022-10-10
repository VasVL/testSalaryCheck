package salaryCheck.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javax.xml.bind.annotation.XmlElement;

public class ExpenseType {

    private String name;
    private BooleanProperty isActive;

    public ExpenseType() {
        this("");
    }

    public ExpenseType(String name) {
        this.name = name;
        this.isActive = new SimpleBooleanProperty(true);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return name;
    }
}
