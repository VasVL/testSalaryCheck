package salaryCheck.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import salaryCheck.model.AppData;
import salaryCheck.model.Employee;


public class EmployeeAddDialogController {

    private Stage dialogStage;
    private AppData appData;
    private Employee tempEmployee;

    @FXML
    private TextField nameTextField;


    public EmployeeAddDialogController() {

        tempEmployee = new Employee();
        appData = AppData.getInstance();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private boolean handleApply(){

        tempEmployee.setName(nameTextField.getText());

        return tempEmployee.getName() != null && !tempEmployee.getName().equals("");
    }

    @FXML
    private void handleOkButton(){

        if(handleApply()) {
            appData.getEmployees().add(tempEmployee);
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancelButton(){
        dialogStage.close();
    }
}
