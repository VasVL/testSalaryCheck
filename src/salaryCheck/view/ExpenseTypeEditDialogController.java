package salaryCheck.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import salaryCheck.model.*;


public class ExpenseTypeEditDialogController {

    private int expenseTypeIndex;
    private boolean isAlreadyExist;
    private Stage dialogStage;
    private AppData appData;
    private ExpenseType tempExpenseType;

    @FXML
    private TextField expenseTypeTextField;


    public ExpenseTypeEditDialogController() {

        isAlreadyExist = true;
        expenseTypeIndex = -1;
        appData = AppData.getInstance();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTempExpenseType(ExpenseType expenseType) {
        tempExpenseType = expenseType;

        if(!appData.getExpenseTypes().contains(expenseType)) {
            isAlreadyExist = false;
        } else {
            isAlreadyExist = true;
            expenseTypeIndex = appData.getExpenseTypes().indexOf(tempExpenseType);
            expenseTypeTextField.setText(tempExpenseType.getName());
        }
    }

    private boolean handleApply(){

        for(ExpenseType expenseType : appData.getExpenseTypes()){
            if(appData.getExpenseTypes().indexOf(expenseType) != expenseTypeIndex && expenseType.getName().equals(expenseTypeTextField.getText())){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.initOwner(dialogStage);
                alert.setTitle("Ошибонька");
                alert.setHeaderText("Такая статья расходов уже есть");
                alert.setContentText("Используйте другое название");

                alert.showAndWait();
                return false;
            }
        }

        return expenseTypeTextField.getText() != null && !expenseTypeTextField.getText().equals("");
    }

    @FXML
    private void handleOkButton(){

        if(handleApply()) {
            tempExpenseType.setName(expenseTypeTextField.getText());
            if(!isAlreadyExist) {
                appData.getExpenseTypes().add(tempExpenseType);
            }
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancelButton(){
        dialogStage.close();
    }
}
