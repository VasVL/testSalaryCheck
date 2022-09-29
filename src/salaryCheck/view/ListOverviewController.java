package salaryCheck.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import salaryCheck.model.AppData;
import salaryCheck.model.Employee;
import salaryCheck.model.Store;

import java.net.URL;
import java.util.ResourceBundle;

public class ListOverviewController implements Initializable {

    private final AppData appData;
    private Stage dialogStage;
    private int startTab;

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

    public ListOverviewController(){

        appData = AppData.getInstance();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setStartTab(int startTab) {
        this.startTab = startTab;

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



    }

    @FXML
    private void handleOkButton(){

        if(true) {

            dialogStage.close();
        }
    }

    @FXML
    private void handleCloseButton(){
        dialogStage.close();
    }
}
