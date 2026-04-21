package gui;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * filename, имя файла со свойствами окон
 * filepath путь, куда сохраняем файл (домашняя директория пользователя)4
 * windowStates - коллекция (ключ - имя окна, значение - объект с параметрами окна).
 */
public class ConfigRead {
    private static final String filename = ".robots_app_config.properties";
    private final String filepath;
    private final Map<String, WindowState> windowStates = new HashMap<>();

    public ConfigRead() { // читаем состояния из файла
        String userHome = System.getProperty("user.home");
        filepath = userHome + File.separator + filename;
        loadFromFile();
    }

    public WindowState getState(String windowName) { // передаем имя окна, получаем состояние
        return windowStates.get(windowName);
    }

    /** Метод, который подгружает данные из файла.
     * Создаем объект properties, открываем файл, загружаем все его содержимое.
     * Парсим ключи, создаём объекты WindowState и сохраняем их в коллекцию.
     * Если файл не существует, метод завершается без ошибок.
     */
    private void loadFromFile() {
        File configFile = new File(filepath);
        if (!configFile.exists()) return;

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(filepath)) {
            props.load(fis);
            for (String key : props.stringPropertyNames()) {
                if (key.startsWith("window.") && key.endsWith(".x")) {
                    String wname = key.substring(7, key.length() - 2);
                    String prefix = "window." + wname;
                    try {
                        int x = Integer.parseInt(props.getProperty(prefix + ".x", "0"));
                        int y = Integer.parseInt(props.getProperty(prefix + ".y", "0"));
                        int width = Integer.parseInt(props.getProperty(prefix + ".width", "400"));
                        int height = Integer.parseInt(props.getProperty(prefix + ".height", "300"));
                        int state = Integer.parseInt(props.getProperty(prefix + ".state", "0"));
                        boolean closed = Boolean.parseBoolean(props.getProperty(prefix + ".closed", "false"));
                        windowStates.put(wname, new WindowState(x, y, width, height, state, closed));
                    } catch (NumberFormatException e) {
                        System.err.println(String.format("Error parsing window state for %s: %s", wname, e.getMessage()));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(String.format("Error loading window configuration: %s", e.getMessage()));
        }
    }
}