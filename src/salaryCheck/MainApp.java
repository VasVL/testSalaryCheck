package salaryCheck;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import salaryCheck.view.OverviewController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class MainApp extends Application {

    private static Stage mainPrimaryStage;

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

            Scene scene = new Scene(root);
            mainPrimaryStage = primaryStage;
            Image icon = new Image("salaryCheck\\sources\\images\\frequency.png");
            primaryStage.getIcons().add(icon);
            mainPrimaryStage.setTitle("Штуки-Дрюки");
            mainPrimaryStage.setScene(scene);
            mainPrimaryStage.show();
        }
        catch (IOException e) {

            System.out.println("Не могу загрузить fxml-файл");
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return mainPrimaryStage;
    }

    public static void main(String[] args) {

        File file = new File("AppData.xml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Application.launch(args);
    }
}