package salaryCheck.view;

import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import salaryCheck.MainApp;
import salaryCheck.model.AppData;
import salaryCheck.model.Employee;
import salaryCheck.model.Expense;
import salaryCheck.model.StoreTableRow;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class OverviewController implements Initializable {

    @FXML
    private TableView<StoreTableRow> storeTableView;

    @FXML
    private TableColumn<StoreTableRow, LocalDate> dateTableColumn;

    @FXML
    private TableColumn<StoreTableRow, Employee> employeeTableColumn;

    @FXML
    private TableColumn<StoreTableRow, Integer> allFeeTableColumn;

    @FXML
    private TableColumn<StoreTableRow, Integer> nonCashTableColumn;

    @FXML
    private TableColumn<StoreTableRow, Integer> cashTableColumn;

    @FXML
    private TableColumn<StoreTableRow, Integer> cashBalanceTableColumn;

    @FXML
    private TableColumn<StoreTableRow, String/*ObservableList<Expense>*/> expensesTableColumn;


    // Ссылка на данные приложения
    public AppData appData;
    public MainApp mainApp = new MainApp();

    /**
     * Конструктор.
     * Конструктор вызывается раньше метода initialize().
     */
    public OverviewController() {
        // Добавление в таблицу данные из наблюдаемого списка
        appData = AppData.getInstance();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        storeTableView.setItems(appData.getStoreTable());

        // Инициализация таблицы
        // Для всех кроме StringProperty нужно добавлять в конце .asObject() (либо переопределять toString())
        dateTableColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        employeeTableColumn.setCellValueFactory(cellData -> cellData.getValue().employeeProperty());
        allFeeTableColumn.setCellValueFactory(cellData -> cellData.getValue().allFeeProperty().asObject());
        nonCashTableColumn.setCellValueFactory(cellData -> cellData.getValue().nonCashProperty().asObject());
        cashTableColumn.setCellValueFactory(cellData -> cellData.getValue().cashProperty().asObject());
        cashBalanceTableColumn.setCellValueFactory(cellData -> cellData.getValue().cashBalanceProperty().asObject());
        // todo
        // Поменял на Bindings.createStringBinding, но это выглядит как костыль, надо использовать интерфейс Observable
        expensesTableColumn.setCellValueFactory(cellData ->
                Bindings.createStringBinding(
                        () ->
                        cellData.getValue().
                        getExpenses().
                        stream().
                        map(Expense::toString).
                        reduce((s1, s2) -> s1 + ";\n" + s2).orElse(""),
                cellData.getValue().expensesProperty())
                /*cellData -> cellData.getValue().expensesProperty().asString()*/
                /*new SimpleStringProperty(
                    cellData.getValue().
                    getExpenses().
                    stream().
                    map(Expense::toString).
                    reduce((s1, s2) -> s1 + ";\n" + s2).orElse("")
                )*/
        );

        //storeTableView.setEditable(true);
        //employeeTableColumn.setEditable(true);
        //allFeeTableColumn.setEditable(true);

        /*
        * Устанавливаем возможность менять значения в таблице
        * Дату изменять нельзя
        * Для сотрудников используем ComboBox, потому что их ограниченное количество
        *
        * */

        employeeTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(appData.getEmployees()));
        employeeTableColumn.setOnEditCommit(editEvent ->
                ((StoreTableRow)editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow())).
                setEmployee(editEvent.getNewValue()));

        allFeeTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        allFeeTableColumn.setOnEditCommit(editEvent ->
                ((StoreTableRow)editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow())).
                setAllFee(editEvent.getNewValue()));

        nonCashTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        nonCashTableColumn.setOnEditCommit(editEvent ->
                ((StoreTableRow)editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow())).
                        setNonCash(editEvent.getNewValue()));

        cashTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        cashTableColumn.setOnEditCommit(editEvent ->
                ((StoreTableRow)editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow())).
                        setCash(editEvent.getNewValue()));

        cashBalanceTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        cashBalanceTableColumn.setOnEditCommit(editEvent ->
                ((StoreTableRow)editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow())).
                        setCashBalance(editEvent.getNewValue()));

        expensesTableColumn.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<StoreTableRow, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<StoreTableRow, String> editEvent) {
                int indexRow = editEvent.getTablePosition().getRow();
                showExpensesEditDialog(indexRow);
            }
        });

        // todo onEditCommit() или onEditCancel() обновлять информацию в tableView
    }

    private void showExpensesEditDialog(int indexRow){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ExpensesEditDialog.fxml"));
        try {
            Parent root = loader.load();
            ExpensesEditDialogController expensesEditController = loader.getController();
            // todo
            // вызвать что ли метод какой
            expensesEditController.setRowIndex(indexRow);

            Stage stage = new Stage();
            stage.setTitle("Внесение расходов");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initOwner(mainApp.getPrimaryStage());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();

            expensesEditController.setDialogStage(stage);

        } catch (IOException e) {
            e.printStackTrace();
            //System.out.println("O-la-la");
        }
    }

    /**
     * Вызывается главным приложением, которое даёт на себя ссылку.
     *
     * //@param mainApp
     */
    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
    }

    @FXML
    private void clearRow(){
        int selectedRow = storeTableView.getSelectionModel().getSelectedIndex();
        // Удалять строку целиком я не буду, потому что все даты должны оставаться в таблице
        //storeTableView.getItems().remove(selectedRow);
        if(selectedRow >= 0) {
            storeTableView.getItems().get(selectedRow).clearRow(appData.getStoreTable(), selectedRow);
        }
    }
}
