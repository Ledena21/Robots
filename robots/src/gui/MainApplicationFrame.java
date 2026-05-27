package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    private final ConfigFile configFile = new ConfigFile(); // Создается снаружи (Правка 10)
    private final WindowConfig windowConfig = WindowConfig.load(configFile); // Фабричный метод (Правка 5)
    private final RobotModel robotModel;

    public MainApplicationFrame(RobotModel model) {
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

    /**
     * Добавляет внутреннее окно в контейнер и восстанавливает его сохранённое состояние.
     */
    private void addSaveableWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        windowConfig.restoreState(frame);
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

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem exitMenuItem = new JMenuItem("Выход", KeyEvent.VK_X);
        exitMenuItem.addActionListener(event -> confirmExit());
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

    private void confirmExit() {
        int result = JOptionPane.showConfirmDialog(
                this, "Вы действительно хотите выйти из программы?",
                "Подтверждение выхода", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            saveAllWindowsAndExit();
        }
    }

    /**
     * Динамически получает все окна из JDesktopPane и сохраняет их состояния.
     * Предотвращает утечки памяти и устраняет необходимость в ArrayList (Правка 6).
     */
    private void saveAllWindowsAndExit() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof Saveable) {
                windowConfig.saveState(frame);
            }
        }
        System.exit(0);
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}