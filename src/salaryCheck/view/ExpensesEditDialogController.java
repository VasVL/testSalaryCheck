package salaryCheck.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import salaryCheck.model.AppData;
import salaryCheck.model.Expense;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

public class ExpensesEditDialogController implements Initializable {

    private int rowIndex;
    private AppData appData;

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
            int i = 0;

            for(Node child : children){
                Integer rowIndex = GridPane.getRowIndex(child);
                if(rowIndex != null && rowIndex == lastRowNumber) {
                    Integer columnIndex = GridPane.getColumnIndex(child);
                    if (columnIndex != null && columnIndex == 0) {
                        ((TextField) child).setText(expense.getAmount().toString());
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

        TextField textField = new TextField();
        textField.setPromptText("Введите сумму...");
        expensesGridPane.add(textField, column++, rowsNumber);

        ObservableList<ChoiceBox<String>> choiceBoxesList = FXCollections.observableArrayList();
        ChoiceBox<String> firstChoiceBox= new ChoiceBox<>(appData.getExpensePurposes());
        HBox hBox = new HBox();
        //for(ChoiceBox<Expense> choiceBox : choiceBoxesList) {
        //    hBox.getChildren().add(choiceBox);
        //}
        choiceBoxesList.add(firstChoiceBox);
        hBox.getChildren().addAll(choiceBoxesList);
        TextField purposeTextField = new TextField();
        hBox.getChildren().add(purposeTextField);
        HBox.setHgrow(purposeTextField, Priority.ALWAYS);
        expensesGridPane.add(/*new ChoiceBox<Expense>()*/hBox, column++, rowsNumber);// todo HBox


        expensesGridPane.add(new ImageView("salaryCheck\\sources\\images\\pencil.png"), column++, rowsNumber);

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
    public void handleApply(){

        appData.getStoreTable().get(rowIndex).getExpenses().clear();

        ObservableList<Node> children = expensesGridPane.getChildren();
        Expense tempExpense = new Expense();

        for(Node child : children){
            Integer columnIndex = GridPane.getColumnIndex(child);
            if(columnIndex != null){
                String inputString = "";
                // 0 колонка - сумма расхода
                if(columnIndex == 0) {
                    inputString = ((TextField) child).getText();
                    int amount = Integer.parseInt(inputString);
                    if (validateAmount()) {
                        tempExpense.setAmount(amount);
                    }
                }
                // 1 колонка - HBox с расходом, состоит из листа choiceBox'ов и одного textField'а
                else if(columnIndex == 1){
                    ObservableList<Node> hBoxChildren = ((HBox)child).getChildren();
                    StringBuilder inputStringBuilder = new StringBuilder("");
                    for(Node hBoxChild : hBoxChildren){
                        if(hBoxChild instanceof ChoiceBox<?>){
                            inputStringBuilder.append(((ChoiceBox<?>) hBoxChild).getValue());
                        } else if(hBoxChild instanceof TextField){
                            inputStringBuilder.append(((TextField)hBoxChild).getText());
                        }
                    }
                    inputString = inputStringBuilder.toString();
                    tempExpense.setPurpose(inputString);

                    appData.getStoreTable().get(rowIndex).addExpense(tempExpense);
                    tempExpense = new Expense();
                }
            }
        }
        System.out.println();
    }

    @FXML
    public void handleOK(){

        handleApply();
        dialogStage.close();
    }

    @FXML
    public void handleClose(){
        dialogStage.close();
    }

}
