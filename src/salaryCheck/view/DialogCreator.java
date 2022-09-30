package salaryCheck.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import salaryCheck.MainApp;
import salaryCheck.model.Employee;
import salaryCheck.model.Store;

import java.io.IOException;

public class DialogCreator {

    public DialogCreator() {
    }

    private Stage createDialog(FXMLLoader loader, String title, Stage owner) throws IOException {
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle(title);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();

        return stage;
    }

    private Stage createDialog(FXMLLoader loader, String title) throws IOException {

        return createDialog(loader, title, MainApp.getPrimaryStage());
    }



    // Очень не нравится, что все эти методы как под копирку одинаковые
    public void showExpensesEditDialog(int indexRow){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ExpensesEditDialog.fxml"));
        try {
            Stage stage = createDialog(loader, "Внесение расходов");

            ExpensesEditDialogController expensesEditController = loader.getController();
            expensesEditController.setDialogStage(stage);
            expensesEditController.setRowIndex(indexRow);

        } catch (IOException e) {
            e.printStackTrace();
            //System.out.println("O-la-la");
        }
    }



    public void showStoreEditDialog(Stage owner, Store editingStore){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("StoreEditDialog.fxml"));
        try {
            Stage stage = createDialog(loader, "Редактирование магазина", owner);

            StoreEditDialogController storeAddController = loader.getController();
            storeAddController.setDialogStage(stage);
            storeAddController.setTempStore(editingStore);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showStoreEditDialog(Stage owner){

        showStoreEditDialog(owner, new Store());
    }

    public void showStoreEditDialog(){
        showStoreEditDialog(MainApp.getPrimaryStage());
    }



    public void showEmployeeEditDialog(Stage owner, Employee employee){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeeEditDialog.fxml"));
        try {
            Stage stage = createDialog(loader, "Добавление сотрудника", owner);

            EmployeeEditDialogController employeeEditController = loader.getController();
            employeeEditController.setDialogStage(stage);
            employeeEditController.setTempEmployee(employee);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showEmployeeEditDialog(Stage owner){
        showEmployeeEditDialog(owner, new Employee());
    }

    public void showEmployeeEditDialog(){
        showEmployeeEditDialog(MainApp.getPrimaryStage());
    }



    public void showExpenseTypeEditDialog(Stage owner, String expenseType){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ExpenseTypeEditDialog.fxml"));
        try {
            Stage stage = createDialog(loader, "Добавление статьи расходов", owner);

            ExpenseTypeEditDialogController expenseTypeEditController = loader.getController();
            expenseTypeEditController.setDialogStage(stage);
            expenseTypeEditController.setTempExpenseType(expenseType);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showExpenseTypeEditDialog(Stage owner){
        showExpenseTypeEditDialog(owner, null);
    }

    public void showExpenseTypeEditDialog(){
        showExpenseTypeEditDialog(MainApp.getPrimaryStage());
    }



    public void showListOverview(int startTab){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ListOverview.fxml"));
        try {
            Stage stage = createDialog(loader, "Список штук");

            ListOverviewController listOverviewController = loader.getController();
            listOverviewController.setDialogStage(stage);
            listOverviewController.setStartTab(startTab);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
