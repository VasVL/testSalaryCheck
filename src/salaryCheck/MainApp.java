package salaryCheck;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import salaryCheck.model.Expense;
import salaryCheck.model.StoreTableRow;
import salaryCheck.view.OverviewController;

import java.io.IOException;
import java.time.LocalDate;

public class MainApp extends Application {

    private ObservableList<StoreTableRow> storeTable = FXCollections.observableArrayList();

    public MainApp() {
        //Для начала сунем всё сюдой
        for(int i = 0; i < 30; i++){
            StoreTableRow storeTableRow = new StoreTableRow();
            storeTableRow.addExpense(new Expense(100, "хоз нужды"));
            storeTableRow.addExpense(new Expense(100, "всяка дрянь"));
            storeTableRow.addExpense(new Expense(100, "др хоз нужды"));
            //storeTableRow.addExpense(new Expense(100, "всяка дрянь"));
            storeTableRow.setDate(LocalDate.now().minusDays(i));
            storeTable.add(storeTableRow);

        }
    }

    public ObservableList<StoreTableRow> getStoreTable() {
        return storeTable;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Overview.fxml"));
            Parent root = loader.load();
            // То же самое
            //Parent root = FXMLLoader.load(getClass().getResource("salaryCheck/view/Overview.fxml"));

            // Даём контроллеру доступ к главному приложению.
            OverviewController controller = loader.getController();
            controller.setMainApp(this);

            Scene scene = new Scene(root, Color.BLACK);
            primaryStage.setTitle("Штуки-Дрюки");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (IOException e) {
            System.out.println("Не могу загрузить fxml-файл");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Application.launch(args);
    }
}