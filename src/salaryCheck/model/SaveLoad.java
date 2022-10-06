package salaryCheck.model;

import javafx.scene.control.Alert;
import salaryCheck.MainApp;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.prefs.Preferences;

public abstract class SaveLoad {

    private static AppData appData;

    /**
     * Возвращает preference файла адресатов, то есть, последний открытый файл.
     * Этот preference считывается из реестра, специфичного для конкретной
     * операционной системы. Если preference не был найден, то возвращается null.
     *
     * @return
     */
    public static File getTableFilePath() {
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
    public static void setTableFilePath(File file) {
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
    public static void loadAppDataFromFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(AppData.class);
            Unmarshaller um = context.createUnmarshaller();

            // Чтение XML из файла и демаршализация.
            appData = (AppData) um.unmarshal(file);

            // добавление новых дней в начало таблиц всех магазинов
            appData.addDaysInTables();

            // Сохраняем путь к файлу в реестре.
            setTableFilePath(file);

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
    public static void saveAppDataToFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(AppData.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Маршаллируем и сохраняем XML в файл.
            appData = AppData.getInstance();
            m.marshal(appData, file);

            // Сохраняем путь к файлу в реестре.
            setTableFilePath(file);

        } catch (Exception e) { // catches ANY exception
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }
}
