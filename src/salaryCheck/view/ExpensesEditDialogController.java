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
import salaryCheck.model.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

public class ExpensesEditDialogController implements Initializable {

    private final int AMOUNT_COLUMN = 0;
    private final int PURPOSES_COLUMN = 1;
    private final AppData appData;

    private int rowIndex;

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

    public void setRowIndex(int rowIndex) {

        this.rowIndex = rowIndex;
        fillGridPane();
    }

    private void fillGridPane(){
        ObservableList<Expense> expenses = appData.getStoreTable().get(rowIndex).getExpenses();

        for(Expense expense : expenses){
            addGridRow();
            ObservableList<Node> children = expensesGridPane.getChildren();
            int lastRowNumber = expensesGridPane.getRowCount() - 1;

            for(Node child : children){
                Integer rowIndex = GridPane.getRowIndex(child);
                if(rowIndex != null && rowIndex == lastRowNumber) {
                    Integer columnIndex = GridPane.getColumnIndex(child);
                    if (columnIndex != null) {
                        if(columnIndex == AMOUNT_COLUMN) {
                            ((TextField) child).setText(expense.getAmount().toString());
                        } else
                        // PURPOSES_COLUMN колонка - HBox с расходом, состоит из choiceBox'ов и одного textField'а
                        if(columnIndex == PURPOSES_COLUMN){
                            ObservableList<Node> hBoxChildren = ((HBox) child).getChildren();

                            // TODO проблема: Когда открывается окно редактирования расходов, если
                            //  они были загружены из XML,обработчик событий вызывается ?дважды? и кидает исключение
                            ((ChoiceBox<String>) hBoxChildren.get(0)).setValue(expense.getExpenseType());

                            if(expense.getExpenseType().equals("Зарплата")) {

                                for (int i = 1; i < hBoxChildren.size(); i++) {
                                    Node hBoxChild = hBoxChildren.get(i);
                                    if (hBoxChild instanceof ChoiceBox<?>) {
                                        if (((ChoiceBox<?>) hBoxChild).getItems().get(0) instanceof Employee) {
                                            ((ChoiceBox<Employee>) hBoxChild).setValue(expense.getEmployee());
                                        } else if (((ChoiceBox<?>) hBoxChild).getItems().get(0) instanceof Store) {
                                            ((ChoiceBox<Store>) hBoxChild).setValue(
                                                    appData.getStores().stream().filter(item ->
                                                            item.getName().equals(expense.getStore())
                                                    ).findAny().orElse(null));
                                        } else if (((ChoiceBox<?>) hBoxChild).getItems().get(0) instanceof LocalDate) {
                                            ((ChoiceBox<LocalDate>) hBoxChild).setValue(expense.getDate());
                                        }
                                    } else if (hBoxChild instanceof TextField && expense.getComment() != null) {
                                        ((TextField) hBoxChild).setText(expense.getComment());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @FXML
    public void addGridRow(){
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

        ChoiceBox<Employee> employeeChoiceBox = new ChoiceBox<>(appData.getEmployees().filtered(Employee::isActive));
        ChoiceBox<Store> storeChoiceBox = new ChoiceBox<>(appData.getStores().filtered(Store::isActive));
        storeChoiceBox.setValue(appData.getCurrentStore());
        ChoiceBox<LocalDate> dateChoiceBox = new ChoiceBox<>();

        // todo одинаковые лямбды надо вынести отдельно
        storeChoiceBox.setOnAction(actionEvent ->
                dateChoiceBox.setItems( FXCollections.observableArrayList(
                        employeeChoiceBox.getValue().getWorkDays().entrySet().stream().filter(entry ->
                                entry.getValue().equals(storeChoiceBox.getValue())
                        ).map(Map.Entry::getKey).toList()
                )));

        // todo селать проверку зп
        employeeChoiceBox.setOnAction( actionEvent ->
                dateChoiceBox.setItems( FXCollections.observableArrayList(
                        employeeChoiceBox.getValue().getWorkDays().entrySet().stream().filter(entry ->
                                entry.getValue().equals(storeChoiceBox.getValue())
                        ).map(Map.Entry::getKey).toList()
                )));


        employeeChoiceBox.setMinWidth(80);
        storeChoiceBox.setMinWidth(80);
        dateChoiceBox.setMinWidth(80);

        choiceBoxesList.add( employeeChoiceBox );
        choiceBoxesList.add( storeChoiceBox );
        choiceBoxesList.add( dateChoiceBox );


        ChoiceBox<String> expenseTypeChoiceBox= new ChoiceBox<>(appData.getExpenseTypes());
        HBox hBox = new HBox();
        hBox.getChildren().add(expenseTypeChoiceBox);
        expenseTypeChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                if(expenseTypeChoiceBox.getValue().equals("Зарплата")){
                    hBox.getChildren().addAll(1, choiceBoxesList);
                } else {
                    if(hBox.getChildren().contains(employeeChoiceBox)){
                        hBox.getChildren().removeAll(choiceBoxesList);
                    }
                }
            }
        });

        //hBox.getChildren().add(expenseTypeChoiceBox);

        /*
         * Строка состоит из : 2.2 - текстовое поле под комментарий
         * Комментарий добавлен в HBox
         *
         * */

        TextField commentTextField = new TextField();
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

    @FXML
    public void removeLastGridRow(){
        if(expensesGridPane.getRowCount() > 1) {
            removeGridRow(expensesGridPane.getRowCount() - 1);
        }
    }

    @FXML
    public void removeGridRow(int rowNumber){
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
    * todo сделать валидацию интеджеров
    * */

    private boolean validateAmount(){
        return true;
    }

    // todo здесь вставить проверку на null
    @FXML
    public boolean handleApply(){

        ObservableList<Expense> tempExpenses = FXCollections.observableArrayList();

        ObservableList<Node> children = expensesGridPane.getChildren();
        Expense tempExpense = new Expense();

        for(Node child : children){
            Integer columnIndex = GridPane.getColumnIndex(child);
            if(columnIndex != null){
                String inputString = "";
                if(columnIndex == AMOUNT_COLUMN) {
                    inputString = ((TextField) child).getText();
                    int amount = Integer.parseInt(inputString);
                    if (validateAmount()) {
                        tempExpense.setAmount(amount);
                    }
                }
                // PURPOSES_COLUMN колонка - HBox с расходом, состоит из одного или нескольких choiceBox'ов и одного textField'а
                else if(columnIndex == PURPOSES_COLUMN){
                    ObservableList<Node> hBoxChildren = ((HBox)child).getChildren();
                    StringBuilder inputStringBuilder = new StringBuilder("");
                    for(Node hBoxChild : hBoxChildren){
                        if(hBoxChild instanceof ChoiceBox<?>){
                            if(((ChoiceBox<?>)hBoxChild).getValue() instanceof String) {
                                tempExpense.setExpenseType(((ChoiceBox<String>)hBoxChild).getValue());
                            } else
                            if(((ChoiceBox<?>)hBoxChild).getValue() instanceof Employee){
                                tempExpense.setEmployee(((ChoiceBox<Employee>)hBoxChild).getValue());
                            } else
                            if (((ChoiceBox<?>)hBoxChild).getValue() instanceof Store) {
                                tempExpense.setStore(((ChoiceBox<Store>)hBoxChild).getValue().getName());
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

                    if(tempExpense.getExpenseType().equals("")){
                        return false;
                    }

                    if(tempExpense.getExpenseType().equals("Зарплата") &&
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

        appData.getStoreTable().get(rowIndex).getExpenses().clear();
        appData.getStoreTable().get(rowIndex).addAllExpenses(tempExpenses);

        return true;
    }

    @FXML
    public void handleOK(){

        if(handleApply()) {
            dialogStage.close();
        }
    }

    @FXML
    public void handleClose(){
        dialogStage.close();
    }

}
