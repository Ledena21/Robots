package gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.JInternalFrame;

/**
 * Менеджер состояний окон.
 * Хранит коллекцию состояний и делегирует чтение и запись классу ConfigFile.
 */
public class WindowConfig {
    private final Map<String, WindowState> windowStates = new HashMap<>();
    private final ConfigFile configFile;

    // Конструктор приватный для реализации фабричного метода (Правка 5)
    // Зависимость внедряется извне (Правка 10)
    private WindowConfig(ConfigFile configFile) {
        this.configFile = configFile;
    }

    /**
     * Фабричный метод для создания менеджера конфигурации и загрузки сохранённых состояний.
     * Защищает конструктор от сайд-эффектов ввода-вывода (Правка 5).
     */
    public static WindowConfig load(ConfigFile configFile) {
        WindowConfig config = new WindowConfig(configFile);
        config.loadFromStorage();
        return config;
    }

    /**
     * Восстанавливает и применяет сохранённое состояние для переданного окна.
     */
    public void restoreState(JInternalFrame frame) {
        if (!(frame instanceof Saveable)) {
            return;
        }

        Saveable saveable = (Saveable) frame;
        WindowState savedState = windowStates.get(saveable.getWindowName());

        if (savedState != null) {
            frame.setSize(savedState.width(), savedState.height());
            frame.setLocation(savedState.x(), savedState.y());
            try {
                if (savedState.state() == WindowStateType.MAXIMIZED) {
                    frame.setMaximum(true);
                } else if (savedState.state() == WindowStateType.ICONIFIED) {
                    frame.setIcon(true);
                }
                frame.setClosed(savedState.isClosed());
            } catch (java.beans.PropertyVetoException ignored) {
                // Игнорируем вето Swing-компонентов при восстановлении геометрии
            }
        }
    }

    /**
     * Считывает геометрию окна, сохраняет её в коллекцию и сразу же записывает изменения в файл (Правка 4, 7).
     */
    public void saveState(JInternalFrame frame) {
        if (!(frame instanceof Saveable)) {
            return;
        }
        Saveable saveable = (Saveable) frame;
        String windowName = saveable.getWindowName();

        // Логика сбора параметров перенесена из окон сюда (Правка 4)
        WindowStateType type = frame.isMaximum() ? WindowStateType.MAXIMIZED
                : (frame.isIcon() ? WindowStateType.ICONIFIED : WindowStateType.NORMAL);

        WindowState state = new WindowState(
                frame.getX(),
                frame.getY(),
                frame.getWidth(),
                frame.getHeight(),
                type,
                frame.isClosed()
        );

        windowStates.put(windowName, state);
        saveToStorage(); // Сохранение в файл происходит автоматически (Правка 7)
    }

    /**
     * Подгружает данные из хранилища.
     */
    private void loadFromStorage() {
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

                    int stateNum = Integer.parseInt(props.getProperty(prefix + ".state", "0"));
                    WindowStateType stateType = WindowStateType.fromNum(stateNum).orElse(WindowStateType.NORMAL);

                    boolean closed = Boolean.parseBoolean(props.getProperty(prefix + ".closed", "false"));

                    windowStates.put(wname, new WindowState(x, y, width, height, stateType, closed));
                } catch (NumberFormatException e) {
                    System.err.println(String.format("Error parsing window state for %s: %s", wname, e.getMessage()));
                }
            }
        }
    }

    /**
     * Записывает свойства всей коллекции в файл.
     */
    private void saveToStorage() {
        Properties props = new Properties();
        for (Map.Entry<String, WindowState> entry : windowStates.entrySet()) {
            String name = entry.getKey();
            WindowState state = entry.getValue();
            props.setProperty("window." + name + ".x", String.valueOf(state.x()));
            props.setProperty("window." + name + ".y", String.valueOf(state.y()));
            props.setProperty("window." + name + ".width", String.valueOf(state.width()));
            props.setProperty("window." + name + ".height", String.valueOf(state.height()));
            props.setProperty("window." + name + ".state", String.valueOf(state.state().getNum()));
            props.setProperty("window." + name + ".closed", String.valueOf(state.isClosed()));
        }
        configFile.save(props);
    }
}