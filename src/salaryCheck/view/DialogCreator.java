package salaryCheck.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import salaryCheck.MainApp;
import salaryCheck.model.Employee;
import salaryCheck.model.ExpenseType;
import salaryCheck.model.Store;
import salaryCheck.model.StoreTableRow;

import java.io.IOException;

public class DialogCreator {

    public DialogCreator() {
    }

    private Stage createDialog(FXMLLoader loader, String title, Stage owner, Image icon) throws IOException {
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.getIcons().add(icon);
        stage.setTitle(title);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);

        return stage;
    }

    private Stage createDialog(FXMLLoader loader, String title, Image image) throws IOException {

        return createDialog(loader, title, MainApp.getPrimaryStage(), image);
    }



    // Очень не нравится, что все эти методы как под копирку одинаковые
    public void showExpensesEditDialog(StoreTableRow tableRow){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ExpensesEditDialog.fxml"));
        try {

            Stage stage = createDialog(loader, "Внесение расходов за " + tableRow.getDate(), new Image("salaryCheck\\sources\\images\\dollar-symbol.png"));
            ExpensesEditDialogController expensesEditController = loader.getController();
            expensesEditController.setDialogStage(stage);
            expensesEditController.setRow(tableRow);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            //System.out.println("O-la-la");
        }
    }



    public void showStoreEditDialog(Stage owner, Store editingStore){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("StoreEditDialog.fxml"));
        try {
            Stage stage = createDialog(loader, "Редактирование магазина", owner, new Image("salaryCheck\\sources\\images\\store.png"));

            StoreEditDialogController storeEditController = loader.getController();
            storeEditController.setDialogStage(stage);
            storeEditController.setTempStore(editingStore);

            stage.showAndWait();

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
            Stage stage = createDialog(loader, "Добавление сотрудника", owner, new Image("salaryCheck\\sources\\images\\user.png"));

            EmployeeEditDialogController employeeEditController = loader.getController();
            employeeEditController.setDialogStage(stage);
            employeeEditController.setTempEmployee(employee);

            stage.showAndWait();

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



    public void showExpenseTypeEditDialog(Stage owner, ExpenseType expenseType){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ExpenseTypeEditDialog.fxml"));
        try {
            Stage stage = createDialog(loader, "Добавление статьи расходов", owner, new Image("salaryCheck\\sources\\images\\dollar-symbol.png"));

            ExpenseTypeEditDialogController expenseTypeEditController = loader.getController();
            expenseTypeEditController.setDialogStage(stage);
            expenseTypeEditController.setTempExpenseType(expenseType);

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showExpenseTypeEditDialog(Stage owner){
        showExpenseTypeEditDialog(owner, new ExpenseType());
    }

    public void showExpenseTypeEditDialog(){
        showExpenseTypeEditDialog(MainApp.getPrimaryStage());
    }



    public void showListOverview(int startTab){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ListOverview.fxml"));
        try {
            Stage stage = createDialog(loader, "Список штук", new Image("salaryCheck\\sources\\images\\list.png"));

            ListOverviewController listOverviewController = loader.getController();
            listOverviewController.setDialogStage(stage);
            listOverviewController.setStartTab(startTab);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
