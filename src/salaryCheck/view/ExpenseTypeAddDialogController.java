package salaryCheck.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import salaryCheck.model.AppData;


public class ExpenseTypeAddDialogController {

    private Stage dialogStage;
    private AppData appData;
    private String tempExpenseType;

    @FXML
    private TextField expenseTypeTextField;


    public ExpenseTypeAddDialogController() {

        appData = AppData.getInstance();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private boolean handleApply(){

        tempExpenseType = expenseTypeTextField.getText();

        return tempExpenseType != null && !tempExpenseType.equals("");
    }

    @FXML
    private void handleOkButton(){

        if(handleApply()) {
            appData.getExpenseTypes().add(tempExpenseType);
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancelButton(){
        dialogStage.close();
    }
}
