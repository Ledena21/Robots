package gui;
import java.util.Properties;
import java.util.Optional;
/**
 * Менеджер состояний окон.
 * Преобразует состояния окон в свойства и делегирует
 * чтение и запись классу ConfigFile.
 */
public class WindowStateManager {
    private final ConfigFile configFile;
    private Properties props;

    // конструктор с параметром, принимает файл извне
    public WindowStateManager(ConfigFile configFile) {
        this.configFile = configFile;
        props = configFile.load(); // читаем состояния из файла при создании
    }

    public Optional<WindowState> getState(String windowName) {
        String prefix = "window." + windowName;
        if (props.getProperty(prefix + ".x") == null) return Optional.empty();

        try {
            int x      = Integer.parseInt(props.getProperty(prefix + ".x", "0"));
            int y      = Integer.parseInt(props.getProperty(prefix + ".y", "0"));
            int width  = Integer.parseInt(props.getProperty(prefix + ".width", "400"));
            int height = Integer.parseInt(props.getProperty(prefix + ".height", "300"));
            int state  = Integer.parseInt(props.getProperty(prefix + ".state", "0"));
            boolean closed = Boolean.parseBoolean(props.getProperty(prefix + ".closed", "false"));
            return Optional.of(new WindowState(x, y, width, height, state, closed));
        } catch (NumberFormatException e) {
            System.err.println(String.format("Error parsing window state for %s: %s", windowName, e.getMessage()));
            return Optional.empty();
        }
    }

    /** Сохраняет состояние окна.
     *  Теперь сразу сохраняет на диск, чтобы избежать рассинхронизации
     *  и чтобы нельзя было "забыть" вызвать сохранение.
     */
    public void saveState(String windowName, WindowState state) {
        String name = windowName;
        props.setProperty("window." + name + ".x",      String.valueOf(state.getX()));
        props.setProperty("window." + name + ".y",      String.valueOf(state.getY()));
        props.setProperty("window." + name + ".width",  String.valueOf(state.getWidth()));
        props.setProperty("window." + name + ".height", String.valueOf(state.getHeight()));
        props.setProperty("window." + name + ".state",  String.valueOf(state.getState()));
        props.setProperty("window." + name + ".closed", String.valueOf(state.isClosed()));

        // сохраняем автоматически
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