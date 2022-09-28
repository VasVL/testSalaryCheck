package salaryCheck.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import salaryCheck.model.AppData;
import salaryCheck.model.Store;


public class StoreAddDialogController {

    private Stage dialogStage;
    private AppData appData;
    private Store tempStore;

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField shiftPayTextField;
    @FXML
    private TextField cleaningPayTextField;
    @FXML
    private TextField salesPercentageTextField;


    public StoreAddDialogController() {

        tempStore = new Store();
        appData = AppData.getInstance();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private boolean handleApply(){

        tempStore.setName(nameTextField.getText());
        try {
            tempStore.setShiftPay(Integer.parseInt(shiftPayTextField.getText()));
            tempStore.setCleaningPay(Integer.parseInt(cleaningPayTextField.getText()));
            tempStore.setSalesPercentage(Integer.parseInt(salesPercentageTextField.getText()) * 0.01);
        } catch (NumberFormatException e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(dialogStage);
            alert.setTitle("Ошибонька");
            alert.setHeaderText("Неверно введены плата или процент");
            alert.setContentText("Введите плату за смену/уборку и процент за выход цифрами");

            alert.showAndWait();
        }

        return tempStore.getName() != null && !tempStore.getName().equals("") &&
                tempStore.getShiftPay() != null &&
                tempStore.getCleaningPay() != null &&
                tempStore.getSalesPercentage() != null;
    }

    @FXML
    private void handleOkButton(){

        if(handleApply()) {
            appData.getStores().add(tempStore);
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancelButton(){
        dialogStage.close();
    }
}
