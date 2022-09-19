package salaryCheck.view;

import javafx.beans.property.ListProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import salaryCheck.model.Expense;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

public class ExpensesEditDialogController implements Initializable {

    private Stage dialogStage;

    private ListProperty<Expense> expensesListProperty;

    @FXML
    private GridPane expensesGridPane;





    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setExpenseListProperty(ObservableList<Expense> expenseListProperty) {
        this.expensesListProperty.set(expenseListProperty);
    }

    @FXML
    public void addGridRow(){
        int rowsNumber = expensesGridPane.getRowCount();
        expensesGridPane.add(new TextField(), 0, rowsNumber);
        expensesGridPane.add(new ChoiceBox<Expense>(), 1, rowsNumber);// todo HBox
        expensesGridPane.add(new ImageView("salaryCheck\\sources\\images\\pencil.png"), 2, rowsNumber);
        expensesGridPane.add(new ImageView("salaryCheck\\sources\\images\\minus.png"), 3, rowsNumber);
    }

    @FXML
    public void removeGridRow(){
        if(expensesGridPane.getRowCount() > 1) {
            ObservableList<Node> children = expensesGridPane.getChildren();
            //children.remove(expensesGridPane.getRowCount()* expensesGridPane.getColumnCount() - 3,
            //        expensesGridPane.getRowCount()* expensesGridPane.getColumnCount() + 1);
            Iterator<Node> iter = children.iterator();
            while(iter.hasNext()){
                Node child = iter.next();
                if(GridPane.getRowIndex(child) != null && GridPane.getRowIndex(child) == expensesGridPane.getRowCount() - 1){
                    iter.remove();
                }
            }
        }
    }

    @FXML
    public void handleOK(){

        dialogStage.close();
    }

    @FXML
    public void handleClose(){
        dialogStage.close();
    }

}
