package salaryCheck.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import salaryCheck.model.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class ExpensesEditDialogController implements Initializable {

    private final int AMOUNT_COLUMN = 0;
    private final int PURPOSES_COLUMN = 1;
    private final AppData appData;
    private StoreTableRow currentStoreTableRow;

    private Stage dialogStage;

    @FXML
    private GridPane expensesGridPane;

    public ExpensesEditDialogController() {

        appData = AppData.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setRow(StoreTableRow storeTableRow) {

        this.currentStoreTableRow = storeTableRow;
        fillGridPane();
    }

    private void fillGridPane(){

        ObservableList<Expense> expenses = currentStoreTableRow.getExpenses();

        for(Expense expense : expenses){
            addGridRow(false);
            ObservableList<Node> children = expensesGridPane.getChildren();
            int lastRowNumber = expensesGridPane.getRowCount() - 1;

            for(Node child : children){
                Integer rowIndex = GridPane.getRowIndex(child);
                if(rowIndex != null && rowIndex == lastRowNumber) {
                    Integer columnIndex = GridPane.getColumnIndex(child);
                    if (columnIndex != null) {
                        if(columnIndex == AMOUNT_COLUMN) {
                            ((TextField) child).setText(expense.getAmount().toString());
                            if(!expense.isCorrect()){
                                ((TextField) child).setBackground(StandardStyles.getBackground(StandardStyles.StandardBackgrounds.RED));
                                ((TextField) child).setTooltip(StandardStyles.getTooltip("Забрано больше, чем заработано"));
                            }
                        } else
                        // PURPOSES_COLUMN колонка - HBox с расходом, состоит из choiceBox'ов и одного textField'а
                        if(columnIndex == PURPOSES_COLUMN){
                            ObservableList<Node> hBoxChildren = ((HBox) child).getChildren();

                            // TODO проблема: Когда открывается окно редактирования расходов, если
                            //  они были загружены из XML,обработчик событий вызывается ?дважды? и кидает исключение
                            ((ChoiceBox<ExpenseType>) hBoxChildren.get(0)).setValue(expense.getExpenseType());

                            if(expense.getExpenseType().getName().equals("Зарплата")) {

                                ((ChoiceBox<Employee>) hBoxChildren.get(1)).setValue(expense.getEmployee());
                                ((ChoiceBox<Store>) hBoxChildren.get(2)).setValue(expense.getStore());
                                ((ChoiceBox<LocalDate>) hBoxChildren.get(3)).setValue(expense.getDate());
                            }
                            ((TextField) hBoxChildren.get( hBoxChildren.size() - 1 )).setText(expense.getComment());
                        }
                    }
                }
            }
        }
    }

    @FXML
    private void addGridRow(){
        addGridRow(true);
    }

    private void addGridRow(boolean isEmpty){
        // todo возможно лучше будет сделать так: добавить здесь прямо при создании changeListener'ы ко всем элементам,
        //  которые будут перезаписывать определённые внутри класса глобальные переменные, типа списка расходов
        int rowsNumber = expensesGridPane.getRowCount();
        int column = 0;

        /*
        * Строка состоит из : 1 - текстовое поле под сумму расхода
        *
        * */

        TextField textField = new TextField();
        textField.setPromptText("Введите сумму...");
        expensesGridPane.add(textField, column++, rowsNumber);

        /*
         * Строка состоит из : 2.1 - выбор статьи расходов в ChoiceBos'е
         * Если выбрана "Зарплата", добавляются ещё три Box'а для выбора сотрудника, магазина, на котором он работал и даты выхода
         * После выбора сотрудника или магазина дату можно выбрать только из ограниченного списка:
         * Map<дата, магазин> - свой для каждого сотрудника
         * Все ChoiceBox'ы объединены в один HBox
         *
         * */

        ObservableList<ChoiceBox<?>> choiceBoxesList = FXCollections.observableArrayList();

        ChoiceBox<Employee> employeeChoiceBox = new ChoiceBox<>();
        ChoiceBox<Store> storeChoiceBox = new ChoiceBox<>();
        ChoiceBox<LocalDate> dateChoiceBox = new ChoiceBox<>();
        dateChoiceBox.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if(date == null){
                    return null;
                }

                return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("ru")));
            }
            @Override
            public LocalDate fromString(String s) {
                return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("ru")));
            }
        });


        ChoiceBox<ExpenseType> expenseTypeChoiceBox = new ChoiceBox<>();
        HBox hBox = new HBox();

        if(isEmpty) {
            employeeChoiceBox.setItems(appData.getEmployees().filtered(Employee::getActive));
            storeChoiceBox.setItems(appData.getStores().filtered(Store::getActive));
            expenseTypeChoiceBox.setItems(appData.getExpenseTypes().filtered(ExpenseType::getActive));
        } else {
            employeeChoiceBox.setItems(appData.getEmployees());
            storeChoiceBox.setItems(appData.getStores());
            expenseTypeChoiceBox.setItems(appData.getExpenseTypes());
        }

        storeChoiceBox.setValue(appData.getCurrentStore());


        EventHandler<ActionEvent> updateDateChoiceBox = actionEvent ->{

            ObservableList<LocalDate> dateList = FXCollections.observableArrayList(
                    employeeChoiceBox.getValue().getWorkDays().entrySet().stream().filter(
                            entry -> entry.getValue().equals( storeChoiceBox.getValue() )
                    ).filter(entry -> {

                        Store entryStore = entry.getValue();
                        LocalDate entryDate = entry.getKey();
                        Employee employee = employeeChoiceBox.getValue();

                        int paymentBalance = calculatePaymentBalance(employee, entryStore, entryDate, false);

                        return paymentBalance > 0;
                    }).map(Map.Entry::getKey).toList()
            );
            dateChoiceBox.setItems(dateList);
        };

        storeChoiceBox.setOnAction( updateDateChoiceBox );

        employeeChoiceBox.setOnAction( updateDateChoiceBox );


        employeeChoiceBox.setMinWidth(80);
        storeChoiceBox.setMinWidth(80);
        dateChoiceBox.setMinWidth(80);

        choiceBoxesList.add( employeeChoiceBox );
        choiceBoxesList.add( storeChoiceBox );
        choiceBoxesList.add( dateChoiceBox );


        hBox.getChildren().add(expenseTypeChoiceBox);
        expenseTypeChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                if(expenseTypeChoiceBox.getValue().getName().equals("Зарплата")){
                    hBox.getChildren().addAll(1, choiceBoxesList);
                } else {
                    if(hBox.getChildren().contains(employeeChoiceBox)){
                        hBox.getChildren().removeAll(choiceBoxesList);
                    }
                }
            }
        });

        /*
         * Строка состоит из : 2.2 - текстовое поле под комментарий
         * Комментарий добавлен в HBox
         *
         * */

        TextField commentTextField = new TextField();
        commentTextField.setPromptText("Введите комментарий...");
        hBox.getChildren().add(commentTextField);

        HBox.setHgrow(commentTextField, Priority.ALWAYS);
        expensesGridPane.add(hBox, column++, rowsNumber);

        /*
         * Строка состоит из : 3 - красивый пиксельный карандаш, который ничего не делает
         *
         * */

        expensesGridPane.add(new ImageView("salaryCheck\\sources\\images\\pencil.png"), column++, rowsNumber);

        /*
         * Строка состоит из : 4 - манус, нажатие на который удаляет строку
         *
         * */

        ImageView minus = new ImageView("salaryCheck\\sources\\images\\minus.png");
        expensesGridPane.add(minus, column++, rowsNumber);
        minus.setOnMouseClicked(mouseEvent -> removeGridRow(rowsNumber));
    }

    private int calculatePaymentBalance(Employee employee, Store entryStore, LocalDate entryDate, boolean withoutCurrentTableRow){

        StoreTableRow entryStoreTableRow = entryStore.getStoreTable().stream().filter(tableRow -> tableRow.getDate().equals(entryDate)).findAny().orElse(new StoreTableRow());

        int dayFee = entryStoreTableRow.getAllFee();
        int dayPay = entryStore.getShiftPay() + entryStore.getCleaningPay() + (int)(entryStore.getSalesPercentage() * dayFee);

        int gotPayment = 0;

        for(Store store : appData.getStores()){
            for(StoreTableRow storeTableRow : store.getStoreTable()){
                if(!withoutCurrentTableRow || !storeTableRow.equals(currentStoreTableRow)) {
                    for (Expense expense : storeTableRow.getExpenses()) {
                        if (expense.getExpenseType().getName().equals("Зарплата") &&
                                expense.getEmployee().getName().equals(employee.getName()) &&
                                expense.getDate().equals(entryDate)) {
                            gotPayment += expense.getAmount();
                        }
                    }
                }
            }
        }

        int paymentBalance = dayPay - gotPayment;

        return paymentBalance;
    }

    @FXML
    private void removeLastGridRow(){
        if(expensesGridPane.getRowCount() > 1) {
            removeGridRow(expensesGridPane.getRowCount() - 1);
        }
    }

    @FXML
    private void removeGridRow(int rowNumber){
        ObservableList<Node> children = expensesGridPane.getChildren();
        Iterator<Node> iter = children.iterator();
        while(iter.hasNext()){
            Node child = iter.next();
            Integer rowIndex = GridPane.getRowIndex(child);
            if(rowIndex != null && rowIndex == rowNumber){
                iter.remove();
            }
        }
    }

    /*
    * Метод Integer.parseInt() сам прекрасно справляется с валидацией
    * */
    private boolean validateAmount(int amount){
        return amount > 0;
    }

    // todo здесь вставить проверку на null
    @FXML
    private boolean handleApply(){

        ObservableList<Expense> tempExpenses = FXCollections.observableArrayList();

        ObservableList<Node> children = expensesGridPane.getChildren();
        Expense tempExpense = new Expense();

        int amount = 0;
        TextField amountTextField = new TextField();

        for(Node child : children){
            Integer columnIndex = GridPane.getColumnIndex(child);
            if(columnIndex != null){
                String inputString = "";
                if(columnIndex == AMOUNT_COLUMN) {
                    inputString = ((TextField) child).getText();
                    int amount1 = Integer.parseInt(inputString);
                    if ( validateAmount(amount1) ) {
                        tempExpense.setAmount(amount1);
                        // todo new
                        amountTextField = (TextField)child;
                        amount = Integer.parseInt(((TextField)child).getText());
                    } else {
                        return false;
                    }
                }
                // PURPOSES_COLUMN колонка - HBox с расходом, состоит из одного или нескольких choiceBox'ов и одного textField'а
                else if(columnIndex == PURPOSES_COLUMN){
                    ObservableList<Node> hBoxChildren = ((HBox)child).getChildren();
                    StringBuilder inputStringBuilder = new StringBuilder("");
                    for(Node hBoxChild : hBoxChildren){
                        if(hBoxChild instanceof ChoiceBox<?>){
                            if(((ChoiceBox<?>)hBoxChild).getValue() instanceof ExpenseType) {
                                tempExpense.setExpenseType(((ChoiceBox<ExpenseType>)hBoxChild).getValue());
                                // todo new
                                if(((ChoiceBox<ExpenseType>) hBoxChildren.get(0)).getValue().getName().equals("Зарплата")) {

                                    Employee employee = ((ChoiceBox<Employee>) hBoxChildren.get(1)).getValue();
                                    Store entryStore = ((ChoiceBox<Store>) hBoxChildren.get(2)).getValue();
                                    LocalDate entryDate = ((ChoiceBox<LocalDate>) hBoxChildren.get(3)).getValue();

                                    int paymentBalance = calculatePaymentBalance(employee, entryStore, entryDate, true);

                                    if(paymentBalance - amount >= 0){
                                        amountTextField.setBackground(StandardStyles.getBackground(StandardStyles.StandardBackgrounds.TRANSIENT));
                                        amountTextField.setTooltip(null);
                                        tempExpense.setIsCorrect(true);
                                    } else {
                                        amountTextField.setBackground(StandardStyles.getBackground(StandardStyles.StandardBackgrounds.RED));
                                        amountTextField.setTooltip(StandardStyles.getTooltip("Забрано больше, чем заработано"));
                                        tempExpense.setIsCorrect(false);
                                    }
                                }
                            } else
                            if(((ChoiceBox<?>)hBoxChild).getValue() instanceof Employee){
                                tempExpense.setEmployee(((ChoiceBox<Employee>)hBoxChild).getValue());
                            } else
                            if (((ChoiceBox<?>)hBoxChild).getValue() instanceof Store) {
                                tempExpense.setStore(((ChoiceBox<Store>)hBoxChild).getValue());
                            } else
                            if (((ChoiceBox<?>)hBoxChild).getValue() instanceof LocalDate) {
                                tempExpense.setDate(((ChoiceBox<LocalDate>)hBoxChild).getValue());
                            }
                            inputStringBuilder.append(((ChoiceBox<?>) hBoxChild).getValue()).append(" ");
                        } else if(hBoxChild instanceof TextField){
                            inputStringBuilder.append(((TextField)hBoxChild).getText());
                            tempExpense.setComment(((TextField)hBoxChild).getText());
                        }
                    }

                    if(tempExpense.getExpenseType().getName().equals("")){
                        return false;
                    }

                    if(tempExpense.getExpenseType().getName().equals("Зарплата") &&
                            (tempExpense.getEmployee() == null || tempExpense.getDate() == null) )
                    {
                        return false;
                    }

                    inputString = inputStringBuilder.toString();
                    tempExpense.setPurpose(inputString);
                    tempExpenses.add(tempExpense);

                    tempExpense = new Expense();
                }
            }
        }

        currentStoreTableRow.getExpenses().clear();
        currentStoreTableRow.addAllExpenses(tempExpenses);

        return true;
    }

    @FXML
    private void handleOK(){

        if(handleApply()) {
            dialogStage.close();
        }
    }

    @FXML
    private void handleClose(){
        dialogStage.close();
    }

}
