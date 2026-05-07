package gui;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import log.Logger;
import model.RobotModel;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowStateManager windowStateManager = new WindowStateManager(); // единый менеджер конфигурации
    private final RobotModel robotModel; // передаётся извне
    // массив окон, поддерживающих сохранение состояния
    private final List<Saveable> saveableWindows = new ArrayList<>();

    public MainApplicationFrame(RobotModel model) { // инициализируем главное окно: размер, контент, меню, обработчик закрытия, модель
        this.robotModel = model;
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);
        setContentPane(desktopPane);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addSaveableWindow(createLogWindow());
        addSaveableWindow(new GameWindow(robotModel));
        addSaveableWindow(createCoordinatesWindow());
        setJMenuBar(generateMenuBar());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }

    /**  Если окно типа JInternalFrame, добавляем его в массив saveableWindows и в контейнер.
     * Восстанавливаем состояние из конфига, если оно есть, применяем его.
     * Если окно открыто, делаем его видимым
     */
    private void addSaveableWindow(Saveable window) {
        if (!(window instanceof JInternalFrame)) return;
        JInternalFrame frame = (JInternalFrame) window;
        saveableWindows.add(window);
        desktopPane.add(frame);
        WindowState savedState = windowStateManager.getState(window.getWindowName());
        if (savedState != null) {
            frame.setSize(savedState.getWidth(), savedState.getHeight());
            frame.setLocation(savedState.getX(), savedState.getY());
            try {
                // Используем понятные имена состояний через enum
                if (savedState.getType() == WindowStateType.MAXIMIZED) frame.setMaximum(true);
                else if (savedState.getType() == WindowStateType.ICONIFIED) frame.setIcon(true);
                frame.setClosed(savedState.isClosed());
            } catch (Exception ignored) {}
        }
        frame.setVisible(!frame.isClosed());
    }

    protected LogWindow createLogWindow() {
        LogWindow lw = new LogWindow(Logger.getDefaultLogSource());
        lw.pack();
        return lw;
    }

    protected RobotCoordinatesWindow createCoordinatesWindow() {
        RobotCoordinatesWindow cw = new RobotCoordinatesWindow(robotModel);
        cw.pack();
        return cw;
    }

    /** Создаем контейнер с меню, горячие клавиши
     */
    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem exitMenuItem = new JMenuItem("Выход", KeyEvent.VK_X);
        exitMenuItem.addActionListener(event -> confirmExit()); // при клике на пункт вызывается метод confirmExit(), который показывает диалог подтверждения
        fileMenu.add(exitMenuItem);
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener(event -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));
        lookAndFeelMenu.add(systemLookAndFeel);
        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_U);
        crossplatformLookAndFeel.addActionListener(event -> setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()));
        lookAndFeelMenu.add(crossplatformLookAndFeel);
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_M);
        addLogMessageItem.addActionListener(event -> Logger.debug("Новая строка"));
        testMenu.add(addLogMessageItem);
        menuBar.add(fileMenu);
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        return menuBar;
    }

    private void confirmExit() { // показывает диалог подтверждения перед выходом
        int result = JOptionPane.showConfirmDialog(
                this, "Вы действительно хотите выйти из программы?",
                "Подтверждение выхода", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            saveAllWindowsAndExit();
        }
    }

    /** Сохраняет состояния всех зарегистрированных окон.
     * Проходит по массиву saveableWindows, собирает состояния и записывает в файл.
     * После сохранения завершает работу приложения.
     */
    private void saveAllWindowsAndExit() {
        for (Saveable window : saveableWindows) {
            windowStateManager.saveState(window.getWindowName(), window.getWindowState());
        }
        windowStateManager.save(); // метод переименован, инкапсулирует детали сохранения
        System.exit(0);
    }

    private void setLookAndFeel(String className) { // применяет выбранную тему оформления к интерфейсу
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
