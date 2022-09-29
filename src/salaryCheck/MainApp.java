package salaryCheck;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import salaryCheck.model.AppData;
import salaryCheck.view.OverviewController;

import java.io.IOException;

public class MainApp extends Application {

    Stage primaryStage;

    private AppData appData;

    public MainApp() {

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
            // todo setAppData()
            //appData = AppData.getInstance();
            controller.setMainApp(this);

            Scene scene = new Scene(root);
            this.primaryStage = primaryStage;
            this.primaryStage.setTitle("Штуки-Дрюки");
            this.primaryStage.setScene(scene);
            this.primaryStage.show();
        }
        catch (IOException e) {
            // todo
            System.out.println("Не могу загрузить fxml-файл");
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {

        Application.launch(args);
    }
}