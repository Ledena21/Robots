package gui;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * filename, имя файла со свойствами окон
 * filepath куда сохраняем файл (домашняя директория пользователя)
 * windowStates - коллекция (ключ - имя окна, значение - объект с параметрами окна).
 */
public class ConfigWrite {
    private static final String filename = ".robots_app_config.properties";
    private final String filepath;
    private final Map<String, WindowState> windowStates = new HashMap<>();

    public ConfigWrite() {
        String userHome = System.getProperty("user.home");
        filepath = userHome + File.separator + filename;
    }

    public void saveState(String windowName, WindowState state) { // добавляем состояние окна в коллекцию
        windowStates.put(windowName, state);
    }

    /** Метод, который сохраняет данные из коллекции в файл.
     * Создаем объект Properties, перебираем все записи в windowStates.
     * Для каждого окна формируем ключи формата window.name.property и записываем значения.
     * Сохраняем в файл с помощью props.store().
     * При ошибке записи выводит сообщение в консоль.
     */
    public void saveToFile() {
        Properties props = new Properties();
        for (Map.Entry<String, WindowState> entry : windowStates.entrySet()) {
            String name = entry.getKey();
            WindowState state = entry.getValue();
            props.setProperty("window." + name + ".x", String.valueOf(state.getX()));
            props.setProperty("window." + name + ".y", String.valueOf(state.getY()));
            props.setProperty("window." + name + ".width", String.valueOf(state.getWidth()));
            props.setProperty("window." + name + ".height", String.valueOf(state.getHeight()));
            props.setProperty("window." + name + ".state", String.valueOf(state.getState()));
            props.setProperty("window." + name + ".closed", String.valueOf(state.isClosed()));
        }
        try (FileOutputStream fos = new FileOutputStream(filepath)) {
            props.store(fos, "Application window configuration");
        } catch (IOException e) {
            System.err.println(String.format("Error saving window configuration: %s", e.getMessage()));
        }
    }
}