package salaryCheck.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import salaryCheck.model.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.ResourceBundle;

public class EmployeeDetailsOverviewController implements Initializable {

    private Stage dialogStage;
    private final AppData appData;
    private Employee employee;
    private ObservableList<EmployeeDetailsTableRow> employeeDetailsRowList;

    @FXML
    private Label employeeLabel;
    @FXML
    private TableView<EmployeeDetailsTableRow> employeeDetailsTableView;
    @FXML
    private TableColumn<EmployeeDetailsTableRow, LocalDate> dateTableColumn;
    @FXML
    private TableColumn<EmployeeDetailsTableRow, Store> storeTableColumn;
    @FXML
    private TableColumn<EmployeeDetailsTableRow, Integer> feeTableColumn;
    @FXML
    private TableColumn<EmployeeDetailsTableRow, Integer> dayPayTableColumn;
    @FXML
    private TableColumn<EmployeeDetailsTableRow, String> gotPaymentTableColumn;
    @FXML
    private TableColumn<EmployeeDetailsTableRow, Integer> paymentBalanceTableColumn;

    public EmployeeDetailsOverviewController() {
        this.appData = AppData.getInstance();
        employeeDetailsRowList = FXCollections.observableArrayList();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        employeeLabel.setText(employee.getName());
        fillEmployeeDetailsTable();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        employeeDetailsTableView.setItems(employeeDetailsRowList);

        dateTableColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        storeTableColumn.setCellValueFactory(cellData -> cellData.getValue().storeProperty());
        feeTableColumn.setCellValueFactory(cellData -> cellData.getValue().dayFeeProperty().asObject());
        dayPayTableColumn.setCellValueFactory(cellData -> cellData.getValue().dayPayProperty().asObject());
        gotPaymentTableColumn.setCellValueFactory(cellData -> cellData.getValue().alreadyGotPaymentProperty());
        paymentBalanceTableColumn.setCellValueFactory(cellData -> cellData.getValue().daySalaryBalanceProperty().asObject());
    }

    private void fillEmployeeDetailsTable(){


        employee.getWorkDays().forEach((entryDate, entryStore) -> {
            EmployeeDetailsTableRow employeeDetailsTableRow = new EmployeeDetailsTableRow();

            employeeDetailsTableRow.setDate(entryDate);
            employeeDetailsTableRow.setStore(entryStore);

            StoreTableRow entryStoreTableRow = entryStore.getStoreTable().stream().filter(tableRow -> tableRow.getDate().equals(entryDate)).findAny().orElse(new StoreTableRow());

            int dayFee = entryStoreTableRow.getAllFee();
            employeeDetailsTableRow.setDayFee(dayFee);

            int dayPay = entryStore.getShiftPay() + entryStore.getCleaningPay() + (int)(entryStore.getSalesPercentage() * dayFee);
            employeeDetailsTableRow.setDayPay(dayPay);

            int gotPayment = 0;
            StringBuilder gotPaymentOverviewBuilder = new StringBuilder(); // ?StringBuffer?
            for(Store store : appData.getStores()){
                for(StoreTableRow storeTableRow : store.getStoreTable()){
                    for(Expense expense : storeTableRow.getExpenses()){
                        if(expense.getExpenseType().getName().equals("Зарплата") &&
                                expense.getEmployee().getName().equals(employee.getName()) &&
                                expense.getDate().equals(entryDate)) {
                            gotPayment += expense.getAmount();
                            gotPaymentOverviewBuilder.append(expense.getAmount() + " - на магазине " + store.getName() + " " + expense.getDate() + ";\n");
                        }
                    }
                }
            }
            String gotPaymentOverview = gotPaymentOverviewBuilder.toString();
            employeeDetailsTableRow.setAlreadyGotPayment(gotPaymentOverview);

            int paymentBalance = dayPay - gotPayment;
            employeeDetailsTableRow.setDaySalaryBalance(paymentBalance);

            employeeDetailsRowList.add(employeeDetailsTableRow);
        });

        employeeDetailsRowList.sort(Comparator.comparing(EmployeeDetailsTableRow::getDate));
    }

    @FXML
    private void handleCloseButton(){
        dialogStage.close();
    }
}
