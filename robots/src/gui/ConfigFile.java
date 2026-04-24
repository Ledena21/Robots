package gui;
import java.io.*;
import java.util.Properties;
/**
 * Работает с файлом конфигурации
 */
public class ConfigFile {
    private static final String filename = ".robots_app_config.properties";
    private final File configFile;

    public ConfigFile() {
        String userHome = System.getProperty("user.home");
        configFile = new File(userHome + File.separator + filename);
    }

    /** Загружает свойства из файла. Если файла нет, возвращает пустой объект. */
    public Properties load() {
        Properties props = new Properties();
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
            } catch (IOException e) {
                System.err.println(String.format("Error loading config file: %s", e.getMessage()));
            }
        }
        return props;
    }

    /** Сохраняет свойства в файл. */
    public void save(Properties props) {
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            props.store(fos, "Application window configuration");
        } catch (IOException e) {
            System.err.println(String.format("Error saving config file: %s", e.getMessage()));
        }
    }
}