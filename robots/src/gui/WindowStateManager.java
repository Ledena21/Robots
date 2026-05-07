package gui;
import java.util.Properties;
/**
 * Менеджер состояний окон.
 * Преобразует состояния окон в свойства и делегирует
 * чтение и запись классу ConfigFile.
 * windowStates - коллекция (ключ - имя окна, значение - объект с параметрами окна).
 */
public class WindowStateManager {
    private final ConfigFile configFile = new ConfigFile();
    private Properties props;

    public WindowStateManager() { // читаем состояния из файла при создании
        props = configFile.load();
    }

    public WindowState getState(String windowName) { // передаем имя окна, получаем состояние
        String prefix = "window." + windowName;
        if (props.getProperty(prefix + ".x") == null) return null;
        try {
            int x      = Integer.parseInt(props.getProperty(prefix + ".x", "0"));
            int y      = Integer.parseInt(props.getProperty(prefix + ".y", "0"));
            int width  = Integer.parseInt(props.getProperty(prefix + ".width", "400"));
            int height = Integer.parseInt(props.getProperty(prefix + ".height", "300"));
            int state  = Integer.parseInt(props.getProperty(prefix + ".state", "0"));
            boolean closed = Boolean.parseBoolean(props.getProperty(prefix + ".closed", "false"));
            return new WindowState(x, y, width, height, state, closed);
        } catch (NumberFormatException e) {
            System.err.println(String.format("Error parsing window state for %s: %s", windowName, e.getMessage()));
            return null;
        }
    }

    /** Сохраняет состояние окна сразу на диск. */
    public void saveState(String windowName, WindowState state) { // добавляем состояние окна и сохраняем
        String name = windowName;
        props.setProperty("window." + name + ".x",      String.valueOf(state.getX()));
        props.setProperty("window." + name + ".y",      String.valueOf(state.getY()));
        props.setProperty("window." + name + ".width",  String.valueOf(state.getWidth()));
        props.setProperty("window." + name + ".height", String.valueOf(state.getHeight()));
        props.setProperty("window." + name + ".state",  String.valueOf(state.getState()));
        props.setProperty("window." + name + ".closed", String.valueOf(state.isClosed()));
    }

    /** Сохраняет данные в файл.
     * Поручает запись объекта Properties классу ConfigFile.
     * При ошибке записи выводит сообщение в консоль.
     */
    public void save() {
        configFile.save(props);
    }

    /**
     * Вспомогательный метод для создания состояния окна.
     */
    public static WindowState createWindowState(javax.swing.JInternalFrame frame) {
        WindowStateType type = frame.isMaximum() ? WindowStateType.MAXIMIZED :
                (frame.isIcon() ? WindowStateType.ICONIFIED : WindowStateType.NORMAL);
        return new WindowState(
                frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight(),
                type.number, frame.isClosed()
        );
    }
}
