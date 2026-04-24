package gui;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
/**
 * Менеджер состояний окон.
 * Хранит коллекцию состояний, преобразует их в свойства и делегирует
 * чтение и запись классу ConfigFile.
 * windowStates - коллекция (ключ - имя окна, значение - объект с параметрами окна).
 */
public class WindowConfig {
    private final Map<String, WindowState> windowStates = new HashMap<>();
    private final ConfigFile configFile = new ConfigFile();

    public WindowConfig() { // читаем состояния из файла при создании
        loadFromFile();
    }

    public WindowState getState(String windowName) { // передаем имя окна, получаем состояние
        return windowStates.get(windowName);
    }

    public void saveState(String windowName, WindowState state) { // добавляем состояние окна в коллекцию
        windowStates.put(windowName, state);
    }

    /** Подгружает данные из файла.
     * Запрашивает свойства у ConfigFile, парсит ключи, создаёт объекты WindowState и сохраняет их в коллекцию.
     * Если файл не существует, метод завершается без ошибок.
     */
    private void loadFromFile() {
        Properties props = configFile.load();
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
    }

    /** Сохраняет данные из коллекции в файл.
     * Перебирает все записи в windowStates, формирует ключи формата window.name.property.
     * Поручает запись объекта Properties в файл классу ConfigFile
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
        configFile.save(props);
    }
}