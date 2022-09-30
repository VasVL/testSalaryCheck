package salaryCheck.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import salaryCheck.model.AppData;


public class ExpenseTypeEditDialogController {

    private int expenseTypeIndex;
    private boolean isAlreadyExist;
    private Stage dialogStage;
    private AppData appData;
    private String tempExpenseType;

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

    public void setTempExpenseType(String expenseType) {
        tempExpenseType = expenseType;

        if(!appData.getExpenseTypes().contains(tempExpenseType)) {
            isAlreadyExist = false;
        } else {
            expenseTypeIndex = appData.getExpenseTypes().indexOf(tempExpenseType);
            expenseTypeTextField.setText(tempExpenseType);
        }
    }

    private boolean handleApply(){

        tempExpenseType = expenseTypeTextField.getText();

        for(String expenseType : appData.getExpenseTypes()){
            if(appData.getExpenseTypes().indexOf(expenseType) != expenseTypeIndex && expenseType.equals(tempExpenseType)){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.initOwner(dialogStage);
                alert.setTitle("Ошибонька");
                alert.setHeaderText("Такая статья расходов уже есть");
                alert.setContentText("Используйте другое название");

                alert.showAndWait();
                return false;
            }
        }

        return tempExpenseType != null && !tempExpenseType.equals("");
    }

    @FXML
    private void handleOkButton(){

        if(handleApply()) {
            if(!isAlreadyExist) {
                appData.getExpenseTypes().add(tempExpenseType);
            } else {
                appData.getExpenseTypes().set(expenseTypeIndex, tempExpenseType);
            }
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancelButton(){
        dialogStage.close();
    }
}
