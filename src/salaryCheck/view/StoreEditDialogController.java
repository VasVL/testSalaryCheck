package salaryCheck.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import salaryCheck.model.AppData;
import salaryCheck.model.Store;

import java.util.Locale;


public class StoreEditDialogController {

    private final AppData appData;
    private int storeIndex;
    private boolean isAlreadyExist;
    private Stage dialogStage;
    private Store tempStore;

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField shiftPayTextField;
    @FXML
    private TextField cleaningPayTextField;
    @FXML
    private TextField salesPercentageTextField;


    public StoreEditDialogController() {

        //tempStore = new Store();
        isAlreadyExist = true;
        storeIndex = -1;
        appData = AppData.getInstance();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTempStore(Store store){
        tempStore = store;
        if(!appData.getStores().contains(tempStore)) {
            isAlreadyExist = false;
        } else {
            storeIndex = appData.getStores().indexOf(store);
            nameTextField.setText(tempStore.getName());
            shiftPayTextField.setText(tempStore.getShiftPay().toString());
            cleaningPayTextField.setText(tempStore.getCleaningPay().toString());
            salesPercentageTextField.setText(String.format(Locale.ROOT, "%.1f", tempStore.getSalesPercentage() * 100));
        }
    }

    private boolean handleApply(){

        for(Store store : appData.getStores()){
            if(appData.getStores().indexOf(store) != storeIndex && store.getName().equals(nameTextField.getText())){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.initOwner(dialogStage);
                alert.setTitle("Ошибонька");
                alert.setHeaderText("Магазин с таким названием уже есть");
                alert.setContentText("Используйте другое название");

                alert.showAndWait();
                return false;
            }
        }

        try {
            Integer.parseInt(shiftPayTextField.getText());
            Integer.parseInt(cleaningPayTextField.getText());
            Double.parseDouble(salesPercentageTextField.getText());
        } catch (NumberFormatException e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(dialogStage);
            alert.setTitle("Ошибонька");
            alert.setHeaderText("Неверно введены плата или процент");
            alert.setContentText("Введите плату за смену/уборку и процент за выход цифрами");

            alert.showAndWait();
            return false;
        }

        return nameTextField.getText() != null && !nameTextField.getText().equals("") &&
                shiftPayTextField.getText() != null &&
                cleaningPayTextField.getText() != null &&
                salesPercentageTextField.getText() != null;
    }

    @FXML
    private void handleOkButton(){

        if(handleApply()) {
            tempStore.setName(nameTextField.getText());
            tempStore.setShiftPay(Integer.parseInt(shiftPayTextField.getText()));
            tempStore.setCleaningPay(Integer.parseInt(cleaningPayTextField.getText()));
            tempStore.setSalesPercentage(Double.parseDouble(salesPercentageTextField.getText()) * 0.01);
            if(!isAlreadyExist){

                appData.getStores().add(tempStore);
                // Если что, могём переключаться на только что созданную талицу
                //appData.setCurrentStore(tempStore);
            }
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancelButton(){
        dialogStage.close();
    }
}
