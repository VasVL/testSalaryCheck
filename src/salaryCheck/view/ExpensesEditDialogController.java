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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import salaryCheck.MainApp;
import salaryCheck.model.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
                            if(!expense.getCorrect()){
                                ((TextField) child).setBackground(StandardStyles.getBackground(StandardStyles.StandardBackgrounds.RED));
                                ((TextField) child).setTooltip(StandardStyles.getTooltip("?????????????? ????????????, ?????? ????????????????????"));
                            }
                        } else
                        // PURPOSES_COLUMN ?????????????? - HBox ?? ????????????????, ?????????????? ???? choiceBox'???? ?? ???????????? textField'??
                        if(columnIndex == PURPOSES_COLUMN){
                            ObservableList<Node> hBoxChildren = ((HBox) child).getChildren();

                            // TODO ????????????????: ?????????? ?????????????????????? ???????? ???????????????????????????? ????????????????, ????????
                            //  ?????? ???????? ?????????????????? ???? XML,???????????????????? ?????????????? ???????????????????? ?????????????? ?? ???????????? ????????????????????
                            ((ChoiceBox<ExpenseType>) hBoxChildren.get(0)).setValue(expense.getExpenseType());

                            if(expense.getExpenseType().getName().equals("????????????????")) {

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

        int rowsNumber = expensesGridPane.getRowCount();
        int column = 0;

        /*
        * ???????????? ?????????????? ???? : 1 - ?????????????????? ???????? ?????? ?????????? ??????????????
        *
        * */

        TextField textField = new TextField();
        textField.setPromptText("?????????????? ??????????...");
        expensesGridPane.add(textField, column++, rowsNumber);

        /*
         * ???????????? ?????????????? ???? : 2.1 - ?????????? ???????????? ???????????????? ?? ChoiceBos'??
         * ???????? ?????????????? "????????????????", ?????????????????????? ?????? ?????? Box'?? ?????? ???????????? ????????????????????, ????????????????, ???? ?????????????? ???? ?????????????? ?? ???????? ????????????
         * ?????????? ???????????? ???????????????????? ?????? ???????????????? ???????? ?????????? ?????????????? ???????????? ???? ?????????????????????????? ????????????:
         * Map<????????, ??????????????> - ???????? ?????? ?????????????? ????????????????????
         * ?????? ChoiceBox'?? ???????????????????? ?? ???????? HBox
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
            dateChoiceBox.setValue(null);
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

                if(expenseTypeChoiceBox.getValue().getName().equals("????????????????")){
                    hBox.getChildren().addAll(1, choiceBoxesList);
                } else {
                    if(hBox.getChildren().contains(employeeChoiceBox)){
                        hBox.getChildren().removeAll(choiceBoxesList);
                    }
                }
            }
        });

        /*
         * ???????????? ?????????????? ???? : 2.2 - ?????????????????? ???????? ?????? ??????????????????????
         * ?????????????????????? ???????????????? ?? HBox
         *
         * */

        TextField commentTextField = new TextField();
        commentTextField.setPromptText("?????????????? ??????????????????????...");
        hBox.getChildren().add(commentTextField);

        HBox.setHgrow(commentTextField, Priority.ALWAYS);
        expensesGridPane.add(hBox, column++, rowsNumber);

        /*
         * ???????????? ?????????????? ???? : 3 - ???????????????? ???????????????????? ????????????????, ?????????????? ???????????? ???? ????????????
         *
         * */

        expensesGridPane.add(new ImageView(new Image(Objects.requireNonNull(MainApp.class.getResourceAsStream("sources/images/pencil.png")))), column++, rowsNumber);

        /*
         * ???????????? ?????????????? ???? : 4 - ??????????, ?????????????? ???? ?????????????? ?????????????? ????????????
         *
         * */

        ImageView minus = new ImageView(new Image(Objects.requireNonNull(MainApp.class.getResourceAsStream("sources/images/minus.png"))));
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
                        if (expense.getExpenseType().getName().equals("????????????????") &&
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
    * ?????????? Integer.parseInt() ?????? ?????????????????? ?????????????????????? ?? ????????????????????
    * */
    private boolean validateAmount(int amount){
        return amount > 0;
    }

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
                        amountTextField = (TextField)child;
                        amount = Integer.parseInt(((TextField)child).getText());
                    } else {
                        return false;
                    }
                }
                // PURPOSES_COLUMN ?????????????? - HBox ?? ????????????????, ?????????????? ???? ???????????? ?????? ???????????????????? choiceBox'???? ?? ???????????? textField'??
                else if(columnIndex == PURPOSES_COLUMN){
                    ObservableList<Node> hBoxChildren = ((HBox)child).getChildren();
                    StringBuilder inputStringBuilder = new StringBuilder("");
                    for(Node hBoxChild : hBoxChildren){
                        if(hBoxChild instanceof ChoiceBox<?>){
                            if(((ChoiceBox<?>)hBoxChild).getValue() instanceof ExpenseType) {
                                tempExpense.setExpenseType(((ChoiceBox<ExpenseType>)hBoxChild).getValue());

                                if(((ChoiceBox<ExpenseType>) hBoxChildren.get(0)).getValue().getName().equals("????????????????")) {

                                    Employee employee = ((ChoiceBox<Employee>) hBoxChildren.get(1)).getValue();
                                    Store entryStore = ((ChoiceBox<Store>) hBoxChildren.get(2)).getValue();
                                    LocalDate entryDate = ((ChoiceBox<LocalDate>) hBoxChildren.get(3)).getValue();

                                    int paymentBalance = calculatePaymentBalance(employee, entryStore, entryDate, true);

                                    if(paymentBalance - amount >= 0){
                                        amountTextField.setBackground(StandardStyles.getBackground(StandardStyles.StandardBackgrounds.TRANSIENT));
                                        amountTextField.setTooltip(null);
                                        tempExpense.setCorrect(true);
                                    } else {
                                        amountTextField.setBackground(StandardStyles.getBackground(StandardStyles.StandardBackgrounds.RED));
                                        amountTextField.setTooltip(StandardStyles.getTooltip("???????????? ???? ???? ????????????, ?????? ??????????"));
                                        tempExpense.setCorrect(false);
                                        tempExpense.setErrorMessage("???????????? ???? ???? ????????????, ?????? ??????????");
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

                    if(tempExpense.getExpenseType().getName().equals("????????????????") &&
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
