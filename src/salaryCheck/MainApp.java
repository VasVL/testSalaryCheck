package salaryCheck;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import salaryCheck.model.AppData;
import salaryCheck.view.OverviewController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

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

    /**
     * Возвращает preference файла адресатов, то есть, последний открытый файл.
     * Этот preference считывается из реестра, специфичного для конкретной
     * операционной системы. Если preference не был найден, то возвращается null.
     *
     * @return
     */
    public File getPersonFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * Задаёт путь текущему загруженному файлу. Этот путь сохраняется
     * в реестре, специфичном для конкретной операционной системы.
     *
     * @param file - файл или null, чтобы удалить путь
     */
    public static void setPersonFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());
        } else {
            prefs.remove("filePath");
        }
    }

    /**
     * Загружает информацию из указанного файла.
     *
     * @param file
     */
    public static void loadPersonDataFromFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(AppData.class);
            Unmarshaller um = context.createUnmarshaller();

            // Чтение XML из файла и демаршализация.
            appData = (AppData) um.unmarshal(file);

            // Сохраняем путь к файлу в реестре.
            setPersonFilePath(file);

        } catch (Exception e) { // catches ANY exception
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибонька");
            alert.setHeaderText("Не могу загрузить данные");
            alert.setContentText("Не могу загрузить данные из файла:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    /**
     * Сохраняет текущую информацию в указанном файле.
     *
     * @param file
     */
    public static void savePersonDataToFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(AppData.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Маршаллируем и сохраняем XML в файл.
            m.marshal(appData, file);

            // Сохраняем путь к файлу в реестре.
            setPersonFilePath(file);

        } catch (Exception e) { // catches ANY exception
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    public static void main(String[] args) {

        // todo
        appData = AppData.getInstance();
        File file = new File("AppData.xml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Application.launch(args);
    }
}