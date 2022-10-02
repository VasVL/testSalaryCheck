package salaryCheck.view;

import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import salaryCheck.MainApp;
import salaryCheck.model.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class OverviewController implements Initializable {

    @FXML
    private ComboBox<Store> storeComboBox;

    @FXML
    private Menu selectStoreMenu;
    @FXML
    private MenuItem addStoreMenuItem;
    @FXML
    private MenuItem addEmployeeMenuItem;
    @FXML
    private MenuItem addExpenseTypeMenuItem;
    @FXML
    private MenuItem storesMenuItem;
    @FXML
    private MenuItem employeesMenuItem;
    @FXML
    private MenuItem expenseTypesMenuItem;


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
    private final AppData appData;
    private final DialogCreator dialogCreator;
    public MainApp mainApp = new MainApp();

    /**
     * Конструктор.
     * Конструктор вызывается раньше метода initialize().
     */
    public OverviewController() {
        // Добавление в таблицу данные из наблюдаемого списка
        appData = AppData.getInstance();
        dialogCreator = new DialogCreator();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        storeComboBox.setItems(appData.getStores());
        storeComboBox.setOnAction(event -> appData.setCurrentStore(storeComboBox.getValue()));

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
        );

        /*
        * Устанавливаем возможность менять значения в таблице
        * Дату изменять нельзя
        * Для сотрудников используем ComboBox, потому что их ограниченное количество
        *
        * */

        employeeTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(appData.getEmployees()));
        employeeTableColumn.setOnEditCommit(editEvent ->{
                StoreTableRow currentRow = ((StoreTableRow)editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow()));
                // удаляем рабочий день у сотрудника, который был выбран до этого
                if(editEvent.getOldValue() != null){
                    editEvent.getOldValue().removeWorkDay(currentRow.getDate());
                }
                // добавляем рабочий день новому выбранному сотруднику
                editEvent.getNewValue().addWorkDay(currentRow.getDate(), appData.getCurrentStore());
                currentRow.setEmployee(editEvent.getNewValue()); } );


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
                dialogCreator.showExpensesEditDialog(indexRow);
            }
        });


        // Файл -> Магазины / Продавцы / Статьи расходов
        storesMenuItem.setOnAction(event -> dialogCreator.showListOverview(0));
        employeesMenuItem.setOnAction(event -> dialogCreator.showListOverview(1));
        expenseTypesMenuItem.setOnAction(event -> dialogCreator.showListOverview(2));

        // файл -> Добавить...
        addStoreMenuItem.setOnAction(event -> dialogCreator.showStoreEditDialog());
        addEmployeeMenuItem.setOnAction(event -> dialogCreator.showEmployeeEditDialog());
        addExpenseTypeMenuItem.setOnAction(event -> dialogCreator.showExpenseTypeEditDialog());

        // todo убрать повтор
        selectStoreMenu.getItems().addAll(appData.getStores().stream().map(
                item -> {
                    MenuItem menuItem = new MenuItem(item.getName());
                    menuItem.setOnAction(actionEvent -> {
                        appData.setCurrentStore(item);
                    });
                    return menuItem;
                }
            ).toList());
        // setOnAction добавил, потому что без него при начальном пустом списке магазинов selectStoreMenu отказывался обновляться
        selectStoreMenu.setOnAction(event -> {
            selectStoreMenu.getItems().clear();
            selectStoreMenu.getItems().addAll(appData.getStores().stream().map(
                    item -> {
                        MenuItem menuItem = new MenuItem(item.getName());
                        menuItem.setOnAction(actionEvent -> {
                            appData.setCurrentStore(item);
                            storeComboBox.setValue(item);
                        });
                        return menuItem;
                    }
            ).toList()); } );

        selectStoreMenu.setOnShowing(event -> {
            selectStoreMenu.getItems().clear();
            selectStoreMenu.getItems().addAll(appData.getStores().stream().map(
                item -> {
                    MenuItem menuItem = new MenuItem(item.getName());
                    menuItem.setOnAction(actionEvent -> {
                        appData.setCurrentStore(item);
                        storeComboBox.setValue(item);
                    });
                    return menuItem;
                }
            ).toList()); } );
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
