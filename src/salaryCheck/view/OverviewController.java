package salaryCheck.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import salaryCheck.MainApp;
import salaryCheck.model.Employee;
import salaryCheck.model.Expense;
import salaryCheck.model.StoreTableRow;

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


    // Ссылка на главное приложение
    public MainApp mainApp = new MainApp();

    /**
     * Конструктор.
     * Конструктор вызывается раньше метода initialize().
     */
    public OverviewController() {
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Инициализация таблицы
        // Для всех кроме StringProperty нужно добавлять в конце .asObject() (либо переопределять toString())
        dateTableColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        employeeTableColumn.setCellValueFactory(cellData -> cellData.getValue().employeeProperty());
        allFeeTableColumn.setCellValueFactory(cellData -> cellData.getValue().allFeeProperty().asObject());
        nonCashTableColumn.setCellValueFactory(cellData -> cellData.getValue().nonCashProperty().asObject());
        cashTableColumn.setCellValueFactory(cellData -> cellData.getValue().cashProperty().asObject());
        cashBalanceTableColumn.setCellValueFactory(cellData -> cellData.getValue().cashBalanceProperty().asObject());
        // Эту дрянь я сделал потому что не смог нормально переопределить toString()
        // (хотя есть подозрение, что можно было просто сделать класс-обёртку над списком Expense'ов)
        expensesTableColumn.setCellValueFactory(cellData -> /*cellData.getValue().expensesProperty()*/
                new SimpleStringProperty(
                cellData.getValue().
                getExpenses().
                stream().
                map(Expense::toString).
                reduce((s1, s2) -> s1 + ";\n" + s2).get()
                )
            );

    }

    /**
     * Вызывается главным приложением, которое даёт на себя ссылку.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;

        // Добавление в таблицу данных из наблюдаемого списка
        storeTableView.setItems(mainApp.getStoreTable());
    }
}
