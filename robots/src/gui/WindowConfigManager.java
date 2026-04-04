package gui;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class WindowConfigManager
{
    private static final String filename = ".robots_app_config.properties"; // имя файла конфигурации, одно для всех экземпляров класса и неизменно
    private final String filepath; //путь до файла
    private final Map<String, WindowState> windowstates; // коллекция окон и параметров

    public WindowConfigManager() {
        String userhome = System.getProperty("user.home"); // сохраняем путь к домашней директории пользователя
        filepath = String.format("%s%s%s", userhome, File.separator, filename); // собираем путь
        windowstates = new HashMap<>(); // пустая коллекция для хранения состояний окон
    }

    public void SaveState(String wname, WindowState state) { // принимаем имя окна и его состояние
        windowstates.put(wname, state); // кладем в коллекцию
    }

    public WindowState LoadState(String wname) { // передаем имя окна
        return windowstates.get(wname); // получаем его состояние
    }

    public void SaveToFile() {
        Properties props = new Properties(); //создаем структуру properties для записи в файл

        for (Map.Entry<String, WindowState> entry : windowstates.entrySet()) { // идем по всем записям коллекции
            WindowState state = entry.getValue(); // извлекаем состояние
            String windowName = entry.getKey(); // извлекаем имя окна

            props.setProperty(String.format("window.%s.x", windowName), String.valueOf(state.getX())); // координата x
            props.setProperty(String.format("window.%s.y", windowName), String.valueOf(state.getY())); // координата y
            props.setProperty(String.format("window.%s.width", windowName), String.valueOf(state.getWidth())); // ширина
            props.setProperty(String.format("window.%s.height", windowName), String.valueOf(state.getHeight())); // высота
            props.setProperty(String.format("window.%s.state", windowName), String.valueOf(state.getState())); // состояние
            props.setProperty(String.format("window.%s.closed", windowName), String.valueOf(state.isClosed())); // закрыто ли окно
        }

        try (FileOutputStream fos = new FileOutputStream(filepath)) { // поток для записи в файл
            props.store(fos, ""); // записываем все свойства из props в файл
        }
        catch (IOException e) {
            System.err.println(String.format("Error saving window configuration: %s", e.getMessage()));
        }
    }

    public void loadFromFile() {
        File configFile = new File(filepath);
        if (!configFile.exists()) { // существует ли файл конфигурации
            return;
        }

        Properties props = new Properties(); // объект для чтения свойств

        try (FileInputStream fis = new FileInputStream(filepath)) { // открывает поток для чтения
            props.load(fis); // закгружаем все пары ключ-значение в props

            for (String key : props.stringPropertyNames()) { // перебираем все ключи
                if (key.startsWith("window.") && key.endsWith(".x")) { // проверяем ключ на соответствие шаблону
                    String wname = key.substring(7, key.length() - 2); // убираем префикс и суффикс, извлекаем имя окна
                    String prefix = "window." + wname;

                    try {
                        int x = Integer.parseInt(props.getProperty(prefix + ".x", "0")); // оканчивается на х, если не найдена, то 0
                        int y = Integer.parseInt(props.getProperty(prefix + ".y", "0"));
                        int width = Integer.parseInt(props.getProperty(prefix + ".width", "400"));
                        int height = Integer.parseInt(props.getProperty(prefix + ".height", "300"));
                        int state = Integer.parseInt(props.getProperty(prefix + ".state", "0"));
                        boolean closed = Boolean.parseBoolean(props.getProperty(prefix + ".closed", "false"));

                        WindowState wstate = new WindowState(x, y, width, height, state, closed); // создаем объект состояние с этими значениями
                        windowstates.put(wname, wstate); // кладем в массив
                    }
                    catch (NumberFormatException e) {
                        System.err.println(String.format("Error parsing window state for %s: %s", wname, e.getMessage()));
                    }
                }
            }
        }
        catch (IOException e) {
            System.err.println(String.format("Error loading window configuration: %s", e.getMessage()));
        }
    }

    public static class WindowState {
        private int x;
        private int y;
        private int width;
        private int height;
        private int state; // 0 - normal, 1 - maximized, 2 - iconified
        private boolean closed;

        public WindowState(int x, int y, int width, int height, int state, boolean isClosed) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.state = state;
            this.closed = isClosed;
        }

        public int getX() { return x; }
        public void setX(int x) { this.x = x; }

        public int getY() { return y; }
        public void setY(int y) { this.y = y; }

        public int getWidth() { return width; }
        public void setWidth(int width) { this.width = width; }

        public int getHeight() { return height; }
        public void setHeight(int height) { this.height = height; }

        public int getState() { return state; }
        public void setState(int state) { this.state = state; }

        public boolean isClosed() { return closed; }
        public void setClosed(boolean closed) { this.closed = closed; }
    }
}