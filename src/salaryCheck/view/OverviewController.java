package salaryCheck.view;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import salaryCheck.MainApp;
import salaryCheck.model.*;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class OverviewController implements Initializable {

    @FXML
    private ComboBox<Store> storeComboBox;

    @FXML
    private Menu fileMenu;
    @FXML
    private Menu selectMenu;
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
    private MenuItem saveMenuItem;
    @FXML
    private MenuItem closeMenuItem;


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
    private TableColumn<StoreTableRow, String> expensesTableColumn;


    // Ссылка на данные приложения
    private final AppData appData;
    private final DialogCreator dialogCreator;


    /**
     * Конструктор.
     * Конструктор вызывается раньше метода initialize().
     */
    public OverviewController() {
        // Добавление в таблицу данные из наблюдаемого списка
        appData = AppData.getInstance();
        dialogCreator = new DialogCreator();
        // todo
        SaveLoad.loadAppDataFromFile(new File("AppData.xml"));
        if(!appData.getStores().isEmpty()) {
            appData.fillStoreTables();
            appData.setCurrentStore(appData.getStores().get(0));
        } else {
            appData.setCurrentStore(new Store());
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        storeComboBox.setItems(appData.getStores().filtered(Store::getActive));
        storeComboBox.setValue(appData.getCurrentStore());
        storeComboBox.setOnAction(event -> {
            appData.setCurrentStore(storeComboBox.getValue());
        });



        storeTableView.setItems(appData.getStoreTable());



        // Инициализация таблицы
        // Для всех кроме StringProperty нужно добавлять в конце .asObject() (либо переопределять toString())
        dateTableColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        //employeeTableColumn.setCellValueFactory(cellData -> cellData.getValue().employeeProperty());
        employeeTableColumn.setCellValueFactory(cellData ->
                Bindings.createObjectBinding(
                        () -> cellData.getValue().getEmployee(),
                        appData.getEmployees()
                )
        );
        allFeeTableColumn.setCellValueFactory(cellData -> cellData.getValue().allFeeProperty().asObject());
        nonCashTableColumn.setCellValueFactory(cellData -> cellData.getValue().nonCashProperty().asObject());
        cashTableColumn.setCellValueFactory(cellData -> cellData.getValue().cashProperty().asObject());
        cashBalanceTableColumn.setCellValueFactory(cellData -> cellData.getValue().cashBalanceProperty().asObject());
        expensesTableColumn.setCellValueFactory(cellData ->
                Bindings.createStringBinding(
                        () -> cellData.getValue().
                                getExpenses().
                                stream().
                                map(Expense::toString).
                                reduce((s1, s2) -> s1 + ";\n" + s2).orElse(""),
                        cellData.getValue().getExpenses(), appData.getExpenseTypes()
                )
        );

        /*
        * Устанавливаем возможность менять значения в таблице
        * Дату изменять нельзя
        * Для сотрудников используем ComboBox, потому что их ограниченное количество
        *
        * */

        //employeeTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(appData.getEmployees().filtered(Employee::getActive)));
        employeeTableColumn.setCellFactory(comboBoxTableColumn -> employeeSellCreator());
        employeeTableColumn.setOnEditCommit(editEvent ->{
            StoreTableRow currentRow = ((StoreTableRow)editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow()));
            // удаляем рабочий день у сотрудника, который был выбран до этого
            if(editEvent.getOldValue() != null){
                editEvent.getOldValue().removeWorkDay(currentRow.getDate());
            }
            // добавляем рабочий день новому выбранному сотруднику
            editEvent.getNewValue().addWorkDay(currentRow.getDate(), appData.getCurrentStore());
            currentRow.setEmployee(editEvent.getNewValue());
        } );


        //allFeeTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        allFeeTableColumn.setCellFactory(integerTableColumn -> feeCashNonCashCellCreator());
        allFeeTableColumn.setOnEditCommit(editEvent -> {
            editEvent.getRowValue().setAllFee(editEvent.getNewValue());
        });

        nonCashTableColumn.setCellFactory(integerTableColumn -> feeCashNonCashCellCreator());
        nonCashTableColumn.setOnEditCommit(editEvent -> editEvent.getRowValue().setNonCash(editEvent.getNewValue()));

        cashTableColumn.setCellFactory(integerTableColumn -> feeCashNonCashCellCreator());
        cashTableColumn.setOnEditCommit(editEvent -> editEvent.getRowValue().setCash(editEvent.getNewValue()));

        cashBalanceTableColumn.setCellFactory(integerTableColumn -> cashBalanceCellCreator());
        cashBalanceTableColumn.setOnEditCommit(editEvent -> editEvent.getRowValue().setCashBalance(editEvent.getNewValue()));

        expensesTableColumn.setCellFactory(storeTableRowStringTableColumn -> new TextFieldTableCell<>() {
            @Override
            public void startEdit() {
                //super.startEdit();
                StoreTableRow storeTableRow = getTableRow().getItem();
                dialogCreator.showExpensesEditDialog(storeTableRow);
            }
        });
        /*expensesTableColumn.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<StoreTableRow, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<StoreTableRow, String> editEvent) {

                StoreTableRow storeTableRow = editEvent.getRowValue();
                dialogCreator.showExpensesEditDialog(storeTableRow);
            }
        });*/



        // Файл -> Новое окно: Магазины / Продавцы / Статьи расходов
        storesMenuItem.setOnAction(event -> {
            dialogCreator.showListOverview(0);
        });
        employeesMenuItem.setOnAction(event -> {
            dialogCreator.showListOverview(1);
        });
        expenseTypesMenuItem.setOnAction(event -> {
            dialogCreator.showListOverview(2);
        });

        // файл -> Добавить...
        addStoreMenuItem.setOnAction(event -> dialogCreator.showStoreEditDialog());
        addEmployeeMenuItem.setOnAction(event -> dialogCreator.showEmployeeEditDialog());
        addExpenseTypeMenuItem.setOnAction(event -> dialogCreator.showExpenseTypeEditDialog());

        saveMenuItem.setOnAction(actionEvent -> SaveLoad.saveAppDataToFile(new File("AppData.xml")));
        closeMenuItem.setOnAction(actionEvent -> MainApp.getPrimaryStage().close());

        // todo убрать повтор
        selectStoreMenu.getItems().setAll(appData.getStores().stream().filter(Store::getActive).map(
                item -> {
                    CheckMenuItem menuItem = new CheckMenuItem(item.getName());
                    if(item.equals(appData.getCurrentStore())){
                        menuItem.setSelected(true);
                    }
                    menuItem.setOnAction(actionEvent -> {
                        fileMenu.hide();
                        storeComboBox.setValue(item);
                    });
                    return menuItem;
                }
        ).toList());
        // setOnAction добавил, потому что без него при начальном пустом списке магазинов selectStoreMenu отказывался обновляться
        selectStoreMenu.setOnAction(event ->
            selectStoreMenu.getItems().setAll(appData.getStores().stream().filter(Store::getActive).map(
                item -> {
                    CheckMenuItem menuItem = new CheckMenuItem(item.getName());
                    if(item.equals(appData.getCurrentStore())){
                        menuItem.setSelected(true);
                    }
                    menuItem.setOnAction(actionEvent -> {
                        fileMenu.hide();
                        storeComboBox.setValue(item);
                    });
                    return menuItem;
                }
            ).toList()) );

        selectStoreMenu.setOnShowing(event ->
            selectStoreMenu.getItems().setAll(appData.getStores().stream().filter(Store::getActive).map(
                item -> {
                    CheckMenuItem menuItem = new CheckMenuItem(item.getName());
                    if(item.equals(appData.getCurrentStore())){
                        menuItem.setSelected(true);
                    }
                    menuItem.setOnAction(actionEvent -> {
                        fileMenu.hide();
                        storeComboBox.setValue(item);
                    });
                    return menuItem;
                }
            ).toList()) );
    }


    private ComboBoxTableCell<StoreTableRow, Employee> employeeSellCreator(){

        ComboBoxTableCell<StoreTableRow, Employee> cell = new ComboBoxTableCell<>(appData.getEmployees().filtered(Employee::getActive)){
            @Override
            public void updateItem(Employee employee, boolean b) {
                super.updateItem(employee, b);

                if (employee != null){
                    if (employee.getName().equals("")) {
                        setBackground(new Background(new BackgroundFill(Color.web("#FF7373", 0.5), null, null)));
                        Tooltip tooltip = new Tooltip("Не выбран сотрудник");
                        tooltip.setShowDelay(new Duration(150));
                        tooltip.setFont(Font.font(14));
                        setTooltip(tooltip);
                    } else {
                        setBackground(new Background(new BackgroundFill(Color.web("#67E667", 0.5), null, null)));
                        setTooltip(null);
                    }
                } else {
                    setBackground(new Background(new BackgroundFill(Color.web("#FFFFFF", 0.0), null, null)));
                }
            }
        };

        /*cell.itemProperty().addListener((observableValue, oldValue, newValue) -> {
            if(cell.getIndex() < appData.getStoreTable().size()) {
                if (observableValue.getValue() != null && observableValue.getValue().getName().equals("")) {
                    cell.setBackground(new Background(new BackgroundFill(Color.web("#FF7373", 0.5), null, null)));
                    Tooltip tooltip = new Tooltip("Не выбран сотрудник");
                    tooltip.setShowDelay(new Duration(150));
                    tooltip.setFont(Font.font(14));
                    cell.setTooltip(tooltip);
                    System.out.println("red " + cell.getIndex());
                } else {
                    System.out.println("green " + cell.getIndex());
                    cell.setBackground(new Background(new BackgroundFill(Color.web("#67E667", 0.5), null, null)));
                    cell.setTooltip(null);
                }
            } else {
                System.out.println("white " + cell.getIndex());
                cell.setBackground(new Background(new BackgroundFill(Color.web("#FFFFFF", 0.0), null, null)));
            }
        });*/

        return cell;
    }

    private TextFieldTableCell<StoreTableRow, Integer> feeCashNonCashCellCreator(){

        Tooltip tooltip = new Tooltip("Сумма наличных и терминала не равна общей выручке");
        tooltip.setShowDelay(new Duration(150));
        tooltip.setFont(Font.font(14));

        TextFieldTableCell<StoreTableRow, Integer> cell = new TextFieldTableCell<>(){
            @Override
            // Отвечает за обновление цвета ячейки при смене магазина, прокрутке таблицы и тп
            public void updateItem(Integer integer, boolean b) {
                super.updateItem(integer, b);

                if (integer != null) {
                    if (getTableRow().getItem() != null) {
                        if (isAllFeeBalanceCorrect(getTableRow().getItem())) {
                            setBackground(new Background(new BackgroundFill(Color.web("#67E667", 0.5), null, null)));
                            setTooltip(null);
                            System.out.println("green cell  " + getIndex());
                        } else {
                            setBackground(new Background(new BackgroundFill(Color.web("#FF7373", 0.5), null, null)));
                            setTooltip(tooltip);
                            System.out.println("red cell " + getIndex());
                        }
                    }
                } else {
                    setBackground(new Background(new BackgroundFill(Color.web("#FFFFFF", 0.0), null, null)));
                    System.out.println("white cell " + getIndex());
                }
            }
        };

        cell.setConverter(new IntegerStringConverter());

        // Отвечает за обновление цвета ячейки при изменении значений таблицы
        appData.getStoreTable().addListener((ListChangeListener<StoreTableRow>) change -> updateAllFeeBalanceBackground(cell, tooltip));

        return cell;
    }

    private void updateAllFeeBalanceBackground(TextFieldTableCell<StoreTableRow, Integer> cell, Tooltip tooltip) {
        if(cell.getTableView().getItems().size() > 0 && cell.getIndex() >= 0) {
            if(cell.getIndex() < appData.getCurrentStore().getStoreTable().size()) {
                StoreTableRow storeTableRow = cell.getTableView().getItems().get(cell.getIndex());

                if (isAllFeeBalanceCorrect(storeTableRow)) {
                    cell.setBackground(new Background(new BackgroundFill(Color.web("#67E667", 0.5), null, null)));
                    cell.setTooltip(null);
                    System.out.println("white appdata " + cell.getIndex());
                } else {
                    cell.setBackground(new Background(new BackgroundFill(Color.web("#FF7373", 0.5), null, null)));
                    cell.setTooltip(tooltip);
                    System.out.println("white appdata " + cell.getIndex());
                }
            }
        }
    }

    private boolean isAllFeeBalanceCorrect(StoreTableRow storeTableRow){

        return storeTableRow.getAllFee() == storeTableRow.getCash() + storeTableRow.getNonCash();
    }

    private TextFieldTableCell<StoreTableRow, Integer> cashBalanceCellCreator(){

        Tooltip tooltip = new Tooltip("Сумма остатка и расходов не равна наличным");
        tooltip.setShowDelay(new Duration(150));
        tooltip.setFont(Font.font(14));

        TextFieldTableCell<StoreTableRow, Integer> cell = new TextFieldTableCell<>(){
            @Override
            public void updateItem(Integer integer, boolean b) {
                super.updateItem(integer, b);

                if(integer != null){
                    if(getTableRow().getItem() != null) {
                        if (isCashBalanceCorrect(getTableRow().getItem())) {
                            setBackground(new Background(new BackgroundFill(Color.web("#67E667", 0.5), null, null)));
                            setTooltip(null);
                        } else {
                            setBackground(new Background(new BackgroundFill(Color.web("#FF7373", 0.5), null, null)));
                            setTooltip(tooltip);
                        }
                    }
                } else {
                    setBackground(new Background(new BackgroundFill(Color.web("#FFFFFF", 0.0), null, null)));
                }
            }
        };

        cell.setConverter(new IntegerStringConverter());

        appData.getStoreTable().addListener((ListChangeListener<StoreTableRow>) change -> updateCashBalanceBackground(cell, tooltip));

        return cell;
    }

    private void updateCashBalanceBackground(TextFieldTableCell<StoreTableRow, Integer> cell, Tooltip tooltip){

        if(cell.getTableView().getItems().size() > 0 && cell.getIndex() >= 0) {
            if(cell.getIndex() < appData.getStoreTable().size()) {
                StoreTableRow storeTableRow = cell.getTableView().getItems().get(cell.getIndex());

                if (isCashBalanceCorrect(storeTableRow)) {
                    cell.setBackground(new Background(new BackgroundFill(Color.web("#67E667", 0.5), null, null)));
                    cell.setTooltip(null);
                } else {
                    cell.setBackground(new Background(new BackgroundFill(Color.web("#FF7373", 0.5), null, null)));
                    cell.setTooltip(tooltip);
                }
            }
        }
    }

    private boolean isCashBalanceCorrect(StoreTableRow storeTableRow){

        return storeTableRow.getCash() == storeTableRow.getExpenses().stream().mapToInt(Expense::getAmount).sum() + storeTableRow.getCashBalance();
    }

    @FXML
    private void addRow(){
        LocalDate date = appData.getCurrentStore().getStoreTable().get(appData.getCurrentStore().getStoreTable().size() - 1).getDate().minusDays(1);
        StoreTableRow storeTableRow = new StoreTableRow(
                date,
                new Employee(""),
                0, 0, 0, 0,
                FXCollections.observableArrayList(expense -> new Observable[]{expense.expenseTypeProperty(), expense.purposeProperty()})
        );
        appData.getCurrentStore().addStoreTableRow(storeTableRow);
        appData.fillStoreTable();
    }

    @FXML
    private void removeRow(){

        if(appData.getCurrentStore().getStoreTable().size() > 1) {
            appData.getCurrentStore().removeStoreTableRow(appData.getCurrentStore().getStoreTable().size() - 1);
            appData.fillStoreTable();
        }
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

    @FXML
    private void handleOkButton(){

        SaveLoad.saveAppDataToFile(new File("AppData.xml"));
        MainApp.getPrimaryStage().close();
    }

    @FXML
    private void handleCloseButton(){
        MainApp.getPrimaryStage().close();
    }
}
