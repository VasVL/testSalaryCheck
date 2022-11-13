package salaryCheck;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import salaryCheck.view.OverviewController;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

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
            Image icon = new Image(Objects.requireNonNull(MainApp.class.getResourceAsStream("sources/images/frequency.png")));
            primaryStage.getIcons().add(icon);
            mainPrimaryStage.setTitle("Штуки-Дрюки");
            mainPrimaryStage.setScene(scene);
            mainPrimaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    windowEvent.consume();
                    controller.closeApp();
                }
            });

            mainPrimaryStage.show();
        }
        catch (IOException e) {

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