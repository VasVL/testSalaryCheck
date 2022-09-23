package salaryCheck.view;

import javafx.beans.property.ListProperty;
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
import javafx.stage.Stage;
import org.w3c.dom.Text;
import salaryCheck.MainApp;
import salaryCheck.model.AppData;
import salaryCheck.model.Expense;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

public class ExpensesEditDialogController implements Initializable {

    private int rowIndex;
    private AppData appData;

    private Stage dialogStage;

    private ObservableList<Expense> expensesList;

    @FXML
    private GridPane expensesGridPane;

    @FXML
    private ImageView minusImageView;

    public ExpensesEditDialogController() {
        appData = AppData.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        /*
        * todo сделать mainapp синглтоном (или вынести все листы в отдельный класс)
        * */
        fillGridPane();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setExpensesList(ObservableList<Expense> expensesList) {
        this.expensesList = expensesList;
    }

    public ObservableList<Expense> getExpensesList() {
        return expensesList;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getRowIndex() {
        return rowIndex;
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
        int rowsNumber = expensesGridPane.getRowCount();
        int column = 0;

        TextField textField = new TextField();
        textField.setPromptText("Введите сумму...");
        expensesGridPane.add(textField, column++, rowsNumber);

        //HBox hBox = new HBox();

        expensesGridPane.add(new ChoiceBox<Expense>(), column++, rowsNumber);// todo HBox


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
        //children.remove(expensesGridPane.getRowCount()* expensesGridPane.getColumnCount() - 3,
        //        expensesGridPane.getRowCount()* expensesGridPane.getColumnCount() + 1);
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

    @FXML
    public void handleApply(){

        appData.getStoreTable().get(rowIndex).getExpenses().clear();

        expensesList = FXCollections.observableArrayList();
        ObservableList<Node> children = expensesGridPane.getChildren();

        for(Node child : children){
            Integer columnIndex = GridPane.getColumnIndex(child);
            if(columnIndex != null && columnIndex == 0){
                String inputString = ((TextField)child).getText();
                int amount = Integer.parseInt(inputString);
                if(validateAmount()) {
                    Expense expense = new Expense();
                    expense.setAmount(amount);
                    expense.setPurpose("Буквально ни на что, господи боже");

                    expensesList.add(expense);
                }
            }
        }

        for(Expense expense : expensesList){
            appData.getStoreTable().get(rowIndex).addExpense(expense);
        }
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
