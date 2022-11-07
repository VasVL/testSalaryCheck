package salaryCheck.view;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import salaryCheck.MainApp;
import salaryCheck.model.*;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
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
            appData.updateStoreTables();
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
                        cellData.getValue().getExpenses(), appData.getExpenseTypes(), appData.getEmployees(), appData.getStores()
                )
        );

        /*
        * Устанавливаем возможность менять значения в таблице
        * Дату изменять нельзя
        * Для сотрудников используем ComboBox, потому что их ограниченное количество
        *
        * */

        dateTableColumn.setCellFactory(dateTableColumn -> {

            TextFieldTableCell<StoreTableRow, LocalDate> cell = new TextFieldTableCell<>();
            cell.setConverter(new StringConverter<LocalDate>() {
                @Override
                public String toString(LocalDate date) {
                    return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("ru")));
                }

                @Override
                public LocalDate fromString(String s) {
                    return null;
                }
            });
            return cell;
        });

        //employeeTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(appData.getEmployees().filtered(Employee::getActive)));
        employeeTableColumn.setCellFactory(comboBoxTableColumn -> employeeCellCreator());
        employeeTableColumn.setOnEditCommit(editEvent ->{
            StoreTableRow currentRow = ((StoreTableRow)editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow()));
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

        expensesTableColumn.setCellFactory(stringTableColumn -> expensesCellCreator());



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


    private ComboBoxTableCell<StoreTableRow, Employee> employeeCellCreator(){

        ComboBoxTableCell<StoreTableRow, Employee> cell = new ComboBoxTableCell<>(appData.getEmployees().filtered(Employee::getActive)){
            @Override
            public void updateItem(Employee employee, boolean b) {
                super.updateItem(employee, b);

                Tooltip tooltip = StandardStyles.getTooltip("Не выбран сотрудник");

                if (employee != null){
                    if (employee.getName().equals("")) {
                        setBackground( StandardStyles.getBackground( StandardStyles.StandardBackgrounds.RED ) );
                        setTooltip(tooltip);
                    } else { // todo мне не нравятся такие громоздкие конструкции из множества вложенных циклов и ветвлений
                        if(getTableRow().getItem() != null) {
                            for (Store store : appData.getStores()) {
                                if (!store.equals(appData.getCurrentStore())) {
                                    for (StoreTableRow storeTableRow : store.getStoreTable()) {
                                        if (getTableRow().getItem().getDate().equals(storeTableRow.getDate())
                                                && getTableRow().getItem().getEmployee().getName().equals(storeTableRow.getEmployee().getName())) {
                                            tooltip.setText("Сотрудник уже работал в этот день в магазине " + store);
                                            setBackground( StandardStyles.getBackground( StandardStyles.StandardBackgrounds.RED ) );
                                            setTooltip(tooltip);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                        setBackground( StandardStyles.getBackground(StandardStyles.StandardBackgrounds.GREEN) );
                        setTooltip(null);
                    }
                } else {
                    setBackground( StandardStyles.getBackground(StandardStyles.StandardBackgrounds.TRANSIENT) );
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

    private TextFieldTableCell<StoreTableRow, String> expensesCellCreator(){

        TextFieldTableCell<StoreTableRow, String> textFieldTableCell = new TextFieldTableCell<>() {
            @Override
            public void startEdit() {
                //super.startEdit();
                StoreTableRow storeTableRow = getTableRow().getItem();
                dialogCreator.showExpensesEditDialog(storeTableRow);
            }

            @Override
            public void updateItem(String s, boolean b) {
                super.updateItem(s, b);

                // Здесь неведомым мне образом цвет обновляется даже при изменении колонки с наличными,
                // хотя я отдельно и не прописываю слушателя для неё или для таблицы
                if (s != null) {
                    if (getTableRow().getItem() != null) {
                        if(isEmployeesPaymentCorrect( getTableRow().getItem() )) {
                            if (isExpensesBalanceCorrect( getTableRow().getItem()) ) {
                                setBackground(StandardStyles.getBackground(StandardStyles.StandardBackgrounds.GREEN));
                                setTooltip(null);
                            } else {
                                setBackground(StandardStyles.getBackground(StandardStyles.StandardBackgrounds.RED));
                                setTooltip(StandardStyles.getTooltip("Сумма расходов за день больше чем наличная выручка"));
                            }
                        } else {
                            setBackground(StandardStyles.getBackground(StandardStyles.StandardBackgrounds.RED));
                            setTooltip(StandardStyles.getTooltip("Расход на зп больше, чем нужно"));
                        }
                    }
                } else {
                    setBackground( StandardStyles.getBackground(StandardStyles.StandardBackgrounds.TRANSIENT) );
                }
            }
        };

        return textFieldTableCell;
    }

    private boolean isEmployeesPaymentCorrect(StoreTableRow storeTableRow){

        for(Expense expense : storeTableRow.getExpenses()){
            if(expense.getExpenseType().getName().equals("Зарплата") && !expense.isCorrect()){
                return false;
            }
        }

        return true;
    }

    private boolean isExpensesBalanceCorrect(StoreTableRow storeTableRow){

        return storeTableRow.getCash() >= storeTableRow.getExpenses().stream().mapToInt(Expense::getAmount).sum();
    }

    private TextFieldTableCell<StoreTableRow, Integer> feeCashNonCashCellCreator(){

        Tooltip tooltip = StandardStyles.getTooltip("Сумма наличных и терминала не равна общей выручке");

        TextFieldTableCell<StoreTableRow, Integer> cell = new TextFieldTableCell<>(){
            @Override
            // Отвечает за обновление цвета ячейки при смене магазина, прокрутке таблицы и тп
            public void updateItem(Integer integer, boolean b) {
                super.updateItem(integer, b);

                if (integer != null) {
                    if (getTableRow().getItem() != null) {
                        if (isAllFeeBalanceCorrect(getTableRow().getItem())) {
                            setBackground( StandardStyles.getBackground(StandardStyles.StandardBackgrounds.GREEN) );
                            setTooltip(null);
                        } else {
                            setBackground( StandardStyles.getBackground(StandardStyles.StandardBackgrounds.RED) );
                            setTooltip(tooltip);
                        }
                    }
                } else {
                    setBackground( StandardStyles.getBackground(StandardStyles.StandardBackgrounds.TRANSIENT) );
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
                    cell.setBackground( StandardStyles.getBackground(StandardStyles.StandardBackgrounds.GREEN) );
                    cell.setTooltip(null);
                } else {
                    cell.setBackground( StandardStyles.getBackground(StandardStyles.StandardBackgrounds.RED) );
                    cell.setTooltip(tooltip);
                }
            }
        }
    }

    private boolean isAllFeeBalanceCorrect(StoreTableRow storeTableRow){

        return storeTableRow.getAllFee() == storeTableRow.getCash() + storeTableRow.getNonCash();
    }

    private TextFieldTableCell<StoreTableRow, Integer> cashBalanceCellCreator(){

        Tooltip tooltip = StandardStyles.getTooltip("Сумма остатка и расходов не равна наличным");

        TextFieldTableCell<StoreTableRow, Integer> cell = new TextFieldTableCell<>(){
            @Override
            public void updateItem(Integer integer, boolean b) {
                super.updateItem(integer, b);

                if(integer != null){
                    if(getTableRow().getItem() != null) {
                        if (isCashBalanceCorrect(getTableRow().getItem())) {
                            setBackground( StandardStyles.getBackground(StandardStyles.StandardBackgrounds.GREEN) );
                            setTooltip(null);
                        } else {
                            setBackground( StandardStyles.getBackground(StandardStyles.StandardBackgrounds.RED) );
                            setTooltip(tooltip);
                        }
                    }
                } else {
                    setBackground( StandardStyles.getBackground(StandardStyles.StandardBackgrounds.TRANSIENT) );
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
                    cell.setBackground( StandardStyles.getBackground(StandardStyles.StandardBackgrounds.GREEN) );
                    cell.setTooltip(null);
                } else {
                    cell.setBackground( StandardStyles.getBackground(StandardStyles.StandardBackgrounds.RED) );
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
        StoreTableRow storeTableRow = new StoreTableRow();
        storeTableRow.setDate(date);
        appData.getCurrentStore().addStoreTableRow(storeTableRow);
        appData.fillStoreTable();
    }

    @FXML
    private void removeRow(){

        if(appData.getCurrentStore().getStoreTable().size() > 1) {
            // todo удалять рабочий день у сотрудника
            appData.getCurrentStore().getStoreTable().get(appData.getCurrentStore().getStoreTable().size() - 1).clearRow();
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
            //storeTableView.getItems().get(selectedRow).clearRow(appData.getStoreTable(), selectedRow);
            appData.getStoreTable().get(selectedRow).clearRow();
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
