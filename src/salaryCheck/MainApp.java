package salaryCheck;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import salaryCheck.model.AppData;
import salaryCheck.view.OverviewController;

import java.io.File;
import java.io.IOException;

public class MainApp extends Application {

    private static Stage mainPrimaryStage;

    private static AppData appData;

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
            mainPrimaryStage = primaryStage;
            Image icon = new Image("salaryCheck\\sources\\images\\frequency.png");
            primaryStage.getIcons().add(icon);
            mainPrimaryStage.setTitle("Штуки-Дрюки");
            mainPrimaryStage.setScene(scene);
            mainPrimaryStage.show();
        }
        catch (IOException e) {
            // todo
            System.out.println("Не могу загрузить fxml-файл");
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return mainPrimaryStage;
    }

    public static void main(String[] args) {

        // todo
        //appData = AppData.getInstance();
        File file = new File("AppData.xml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Application.launch(args);
    }
}