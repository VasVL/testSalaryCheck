package salaryCheck.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import salaryCheck.model.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Locale;
import java.util.ResourceBundle;

public class EmployeeDetailsOverviewController implements Initializable {

    private Stage dialogStage;
    private final AppData appData;
    private Employee employee;
    private ObservableList<EmployeeDetailsTableRow> employeeDetailsRowList;
    private ObservableList<EmployeeDetailsTableRow> allEmployeeDetailsRowList;

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
        allEmployeeDetailsRowList = FXCollections.observableArrayList();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        employeeLabel.setText("Рабочие дни " + employee.getName());
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

        dateTableColumn.setCellFactory(dateTableColumn -> {

            TextFieldTableCell<EmployeeDetailsTableRow, LocalDate> cell = new TextFieldTableCell<>();
            cell.setConverter(new StringConverter<LocalDate>() {
                @Override
                public String toString(LocalDate date) {
                    return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("ru")));
                }

                @Override
                public LocalDate fromString(String s) {
                    return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("ru")));
                }
            });
            return cell;
        });
        paymentBalanceTableColumn.setCellFactory(new Callback<TableColumn<EmployeeDetailsTableRow, Integer>, TableCell<EmployeeDetailsTableRow, Integer>>() {
            @Override
            public TableCell<EmployeeDetailsTableRow, Integer> call(TableColumn<EmployeeDetailsTableRow, Integer> employeeDetailsTableRowIntegerTableColumn) {
                TextFieldTableCell<EmployeeDetailsTableRow, Integer> cell = new TextFieldTableCell<>(){
                    @Override
                    public void updateItem(Integer integer, boolean b) {
                        super.updateItem(integer, b);

                        if(integer != null) {
                            if (integer < 0) {
                                setBackground( StandardStyles.getBackground(StandardStyles.StandardBackgrounds.RED) );
                                setTooltip( StandardStyles.getTooltip("Отрицательный остаток: забрано больше, чем нужно") );
                            } else if(integer == 0){
                                setBackground( StandardStyles.getBackground(StandardStyles.StandardBackgrounds.GREEN) );
                                setTooltip(null);
                            } else {
                                setBackground( StandardStyles.getBackground(StandardStyles.StandardBackgrounds.TRANSIENT) );
                                setTooltip(null);
                            }
                        } else {
                            // туту надо, иначе цвет может не обновиться после нажатия "показать/скрыть все смены"
                            setBackground( StandardStyles.getBackground(StandardStyles.StandardBackgrounds.TRANSIENT) );
                            setTooltip(null);
                        }
                    }
                };
                //cell.setConverter(new IntegerStringConverter());
                return cell;
            }
        });
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
                            gotPaymentOverviewBuilder.append(expense.getAmount() + " - на магазине " + store.getName() + " " +
                                    storeTableRow.getDate().format(DateTimeFormatter.ofPattern("dd-го MMM yyyy", Locale.forLanguageTag("ru"))) + ";\n");
                        }
                    }
                }
            }
            String gotPaymentOverview = gotPaymentOverviewBuilder.toString();
            employeeDetailsTableRow.setAlreadyGotPayment(gotPaymentOverview);

            int paymentBalance = dayPay - gotPayment;
            employeeDetailsTableRow.setDaySalaryBalance(paymentBalance);

            if(paymentBalance != 0) {
                employeeDetailsRowList.add(employeeDetailsTableRow);
            }
            allEmployeeDetailsRowList.add(employeeDetailsTableRow);
        });

        employeeDetailsRowList.sort(Comparator.comparing(EmployeeDetailsTableRow::getDate).reversed());
        allEmployeeDetailsRowList.sort(Comparator.comparing(EmployeeDetailsTableRow::getDate).reversed());
    }

    @FXML
    private void handleShowHideAllShiftsButton(){
        if(employeeDetailsTableView.getItems().equals(employeeDetailsRowList)){
            employeeDetailsTableView.setItems(allEmployeeDetailsRowList);
        } else {
            employeeDetailsTableView.setItems(employeeDetailsRowList);
        }
    }

    @FXML
    private void handleCloseButton(){
        dialogStage.close();
    }
}
