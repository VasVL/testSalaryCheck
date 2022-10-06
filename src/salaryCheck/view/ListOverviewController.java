package salaryCheck.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.stream.Collectors;

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

        // todo при таком подходе значения в листе не обновляются автоматически
        storeListView.setItems( appData.getStores().filtered(Store::isActive) );
        employeeListView.setItems( appData.getEmployees().filtered(Employee::isActive) );
        expenseTypeListView.setItems( appData.getExpenseTypes() );

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
            storeListView.setItems( appData.getStores().filtered(Store::isActive) );
        } else if(selectedTab.equals(employeesTab)) {
            dialogCreator.showEmployeeEditDialog(dialogStage);
            employeeListView.setItems( appData.getEmployees().filtered(Employee::isActive) );
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
                storeListView.setItems( appData.getStores().filtered(Store::isActive) );
            }
        } else if(selectedTab.equals(employeesTab)) {
            if(employeeListView.getSelectionModel().getSelectedItem() != null){
                dialogCreator.showEmployeeEditDialog(dialogStage, employeeListView.getSelectionModel().getSelectedItem());
                employeeListView.setItems( appData.getEmployees().filtered(Employee::isActive) );
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
        // Вместо \того сделал переменную isActive
        if(selectedTab.equals(storesTab)){
            if(storeListView.getSelectionModel().getSelectedItem() != null) {
                storeListView.getSelectionModel().getSelectedItem().setActive(false);
                storeListView.setItems( appData.getStores().filtered(Store::isActive) );
            }
        } else if(selectedTab.equals(employeesTab)) {
            if(employeeListView.getSelectionModel().getSelectedItem() != null){
                employeeListView.getSelectionModel().getSelectedItem().setActive(false);
                employeeListView.setItems( appData.getEmployees().filtered(Employee::isActive) );
            }
        } else if(selectedTab.equals(expenseTypesTab)) {
            if(expenseTypeListView.getSelectionModel().getSelectedItem() != null){
                // todo шо-то со строками делать
            }
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
