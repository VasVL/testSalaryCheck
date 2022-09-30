package salaryCheck.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import salaryCheck.model.AppData;
import salaryCheck.model.Employee;


public class EmployeeEditDialogController {

    private int employeeIndex;
    private boolean isAlreadyExist;
    private Stage dialogStage;
    private AppData appData;
    private Employee tempEmployee;

    @FXML
    private TextField nameTextField;


    public EmployeeEditDialogController() {

        isAlreadyExist = true;
        employeeIndex = -1;
        //tempEmployee = new Employee();
        appData = AppData.getInstance();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTempEmployee(Employee employee) {
        tempEmployee = employee;

        if(!appData.getEmployees().contains(tempEmployee)) {
            isAlreadyExist = false;
        } else {
            employeeIndex = appData.getEmployees().indexOf(tempEmployee);
            nameTextField.setText(tempEmployee.getName());
        }
    }

    private boolean handleApply(){

        tempEmployee.setName(nameTextField.getText());

        for(Employee employee : appData.getEmployees()){
            if(appData.getEmployees().indexOf(employee) != employeeIndex && employee.getName().equals(tempEmployee.getName())){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.initOwner(dialogStage);
                alert.setTitle("Ошибонька");
                alert.setHeaderText("Такой сотрудник уже есть");
                alert.setContentText("Используйте другое имя");

                alert.showAndWait();
                return false;
            }
        }

        return tempEmployee.getName() != null && !tempEmployee.getName().equals("");
    }

    @FXML
    private void handleOkButton(){

        if(handleApply()) {
            if(!isAlreadyExist) {
                appData.getEmployees().add(tempEmployee);
            }
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancelButton(){
        dialogStage.close();
    }
}
