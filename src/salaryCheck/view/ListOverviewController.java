package salaryCheck.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import salaryCheck.model.AppData;
import salaryCheck.model.Employee;
import salaryCheck.model.Store;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class ListOverviewController implements Initializable {

    private final AppData appData;
    private final DialogCreator dialogCreator;
    private Stage dialogStage;

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab storesTab;
    @FXML
    private Tab employeesTab;
    @FXML
    private Tab expenseTypesTab;
    @FXML
    private ListView<Store> storeListView;
    @FXML
    private ListView<Employee> employeeListView;
    @FXML
    private ListView<String> expenseTypeListView;
    @FXML
    private Label storeNameLabel;
    @FXML
    private Label storeShiftPayLabel;
    @FXML
    private Label storeCleaningPayLabel;
    @FXML
    private Label storeSalesPercentageLabel;
    @FXML
    private Label employeeSalaryBalanceLabel;

    public ListOverviewController(){

        appData = AppData.getInstance();
        dialogCreator = new DialogCreator();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setStartTab(int startTab) {

        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        switch (startTab){
            case 0 -> selectionModel.select(storesTab);
            case 1 -> selectionModel.select(employeesTab);
            case 2 -> selectionModel.select(expenseTypesTab);
            default -> selectionModel.select(storesTab);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        storeListView.setItems(appData.getStores());
        employeeListView.setItems(appData.getEmployees());
        expenseTypeListView.setItems(appData.getExpenseTypes());

        storeListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> showStoreInfo(newValue));
        employeeListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> showEmployeeInfo(newValue));

    }

    private void showStoreInfo(Store store){
        if(store != null) {
            storeNameLabel.setText(store.getName());
            storeShiftPayLabel.setText(store.getShiftPay().toString());
            storeCleaningPayLabel.setText(store.getCleaningPay().toString());
            storeSalesPercentageLabel.setText(String.format(Locale.ROOT, "%.1f", store.getSalesPercentage() * 100) + "%");
        } else {
            storeNameLabel.setText("");
            storeShiftPayLabel.setText("");
            storeCleaningPayLabel.setText("");
            storeSalesPercentageLabel.setText("");
        }
    }

    private void showEmployeeInfo(Employee employee){
        if(employee != null) {
            employeeSalaryBalanceLabel.setText(employee.getSalaryBalance().toString());
        } else {
            storeNameLabel.setText("");
        }
    }

    @FXML
    private void handleAddButton(){

        Tab selectedTab = tabPane.getSelectionModel().selectedItemProperty().get();

        if(selectedTab.equals(storesTab)){
            dialogCreator.showStoreEditDialog(dialogStage);
        } else if(selectedTab.equals(employeesTab)) {
            dialogCreator.showEmployeeEditDialog(dialogStage);
        } else if(selectedTab.equals(expenseTypesTab)) {
            dialogCreator.showExpenseTypeEditDialog(dialogStage);
        }
    }

    @FXML
    private void handleEditButton(){

        Tab selectedTab = tabPane.getSelectionModel().selectedItemProperty().get();

        if(selectedTab.equals(storesTab)){
            if(storeListView.getSelectionModel().getSelectedItem() != null) {
                dialogCreator.showStoreEditDialog(dialogStage, storeListView.getSelectionModel().getSelectedItem());
            }
        } else if(selectedTab.equals(employeesTab)) {
            if(employeeListView.getSelectionModel().getSelectedItem() != null){
                dialogCreator.showEmployeeEditDialog(dialogStage, employeeListView.getSelectionModel().getSelectedItem());
            }
        } else if(selectedTab.equals(expenseTypesTab)) {
            if(expenseTypeListView.getSelectionModel().getSelectedItem() != null){
                dialogCreator.showExpenseTypeEditDialog(dialogStage, expenseTypeListView.getSelectionModel().getSelectedItem());
            }
        }
    }

    @FXML
    private void handleRemoveButton(){

        Tab selectedTab = tabPane.getSelectionModel().selectedItemProperty().get();
        //todo здесь нужно помнить, что удалять целмком ничего не надо,
        // точнее нужно оставить то, что уже используется в таблице, но скрыть возможность использовать это в дальнейшем
        // Возможно нужно будет сделать два списка для оних и тех же магазинов / сотрудников / расходов : актуальный и архив
        if(selectedTab.equals(storesTab)){

        } else if(selectedTab.equals(employeesTab)) {

        } else if(selectedTab.equals(expenseTypesTab)) {

        }
    }

    @FXML
    private void handleOkButton(){

            dialogStage.close();
    }

    @FXML
    private void handleCloseButton(){
        dialogStage.close();
    }
}