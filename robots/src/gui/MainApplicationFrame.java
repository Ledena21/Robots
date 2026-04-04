package gui;

import java.awt.Dimension; //используется для задания размеров компонентов (ширина, высота)
import java.awt.Toolkit;// класс для работы с системными настройками (например, получение размеров экрана)
import java.awt.event.KeyEvent;//содержит константы для обработки событий клавиатуры
import java.awt.event.WindowAdapter;//добавлен для обработки события закрытия окна
import java.awt.event.WindowEvent;//добавлен для обработки события закрытия окна

import javax.swing.JDesktopPane;// контейнер для внутренних окон
import javax.swing.JFrame;//главное окно приложения
import javax.swing.JInternalFrame;//  внутреннее окно внутри JDesktopPane
import javax.swing.JMenu; //  для создания меню приложения
import javax.swing.JMenuBar;//для создания меню приложения
import javax.swing.JMenuItem;// для создания меню приложения
import javax.swing.JOptionPane;// для отображения диалоговых окон (сообщения, подтверждения, ввод данных)
import javax.swing.SwingUtilities; // класс для работы с Swing например, для потокобезопаснjcnv
import javax.swing.UIManager;// управление внешним видом
import javax.swing.UnsupportedLookAndFeelException;//исключение при попытке установить неподдерживаемый внешний вид

import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame // Наследуемся от JFrame (главное окно ОС)
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    // Менеджер для сохранения и восстановления состояний окон
    private final WindowConfigManager configManager = new WindowConfigManager();
    // Ссылки на окна для сохранения их состояний
    private LogWindow logWindow;
    private GameWindow gameWindow;

    public MainApplicationFrame() {

        configManager.loadFromFile(); // загружаем сохранённые состояния окон из файла конфигурации

        int inset = 50; // отступ
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane); // устанавливаем рабочий стол как содержимое окна


        logWindow = createLogWindow(); // создаем и добавляем окно логов
        addWindow(logWindow, "LogWindow");

        gameWindow = new GameWindow(); // создаем и добавляем игровое окно
        gameWindow.setSize(400,  400);
        addWindow(gameWindow, "GameWindow");

        setJMenuBar(generateMenuBar());// создаем и устанавливаем меню
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // изменяем поведение закрытия: не завершать приложение сразу, а обработать событие
        addWindowListener(new WindowAdapter() {         // добавляем обработчик закрытия главного окна для сохранения состояний
            @Override
            public void windowClosing(WindowEvent e) {
                saveWindowStateAndExit(); // вызывается сохранение и выход
            }
        });
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());// gолучаем источник логов

        // gробуем восстановить сохранённое состояние окна логов
        WindowConfigManager.WindowState savedState = configManager.LoadState("LogWindow");
        if (savedState != null)
        {
            logWindow.setLocation(savedState.getX(), savedState.getY());
            logWindow.setSize(savedState.getWidth(), savedState.getHeight());
            try {
                logWindow.setMaximum(savedState.getState() == 1);
                logWindow.setIcon(savedState.getState() == 2);
            } catch (Exception ex) {
            }
            if (!savedState.isClosed())
            {
                logWindow.setVisible(true);
            }
        }
        else
        {
            // pначения по умолчанию, если конфигурация не найдена
            logWindow.setLocation(10,10);// позиция на рабочем столе
            logWindow.setSize(300, 800);// размер окна
        }

        setMinimumSize(logWindow.getSize());// устанавливаем минимальный размер главного окна
        logWindow.pack();// eпаковываем
        Logger.debug("Протокол работает");//первое сообщение в лог
        return logWindow;
    }
    //Метод добавления окна на рабочий стол
    protected void addWindow(JInternalFrame frame, String windowName)
    {
        desktopPane.add(frame);//Добавляем на рабочий стол

        // для игрового окна также пробуем восстановить состояние
        if ("GameWindow".equals(windowName) && configManager.LoadState(windowName) != null)
        {
            WindowConfigManager.WindowState savedState = configManager.LoadState(windowName);
            frame.setLocation(savedState.getX(), savedState.getY());
            frame.setSize(savedState.getWidth(), savedState.getHeight());
            try {
                frame.setMaximum(savedState.getState() == 1);
                frame.setIcon(savedState.getState() == 2);
            } catch (Exception ex) {
            }
            if (!savedState.isClosed())
            {
                frame.setVisible(true);
            }
        }
        else
        {
            frame.setVisible(true);//делаем видимым
        }
    }

    protected void addWindow(JInternalFrame frame)
    {
        addWindow(frame, frame.getTitle());
    }

    private JMenuBar generateMenuBar()// Создаем панель меню
    {
        JMenuBar menuBar = new JMenuBar();

        // МЕНЮ "Файл"
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F); // Alt + F для быстрого доступа

        // Выход
        JMenuItem exitMenuItem = new JMenuItem("Выход", KeyEvent.VK_X);
        exitMenuItem.addActionListener((event) -> {// addActionListener: Подключает обработчик события. Когда пользователь кликает на пункт меню, выполняется код внутри лямбды.
            confirmExit(); // Вызываем метод подтверждения выхода
        });
        fileMenu.add(exitMenuItem);// Добавляем пункт в меню

        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        {
            // позволяет менять внешний вид программы на системный (как у Windows) или универсальный
            JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
            crossplatformLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        {
            JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> {
                Logger.debug("Новая строка");
            });
            testMenu.add(addLogMessageItem);
        }

        // Добавляем все меню
        menuBar.add(fileMenu);
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);

        return menuBar;
    }

    //         Метод для подтверждения выхода из приложения

    private void confirmExit() {
        // Показываем диалог подтверждения
        int result = JOptionPane.showConfirmDialog(
                this,
                "Вы действительно хотите выйти из программы?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,// Кнопки Да/Нет
                JOptionPane.QUESTION_MESSAGE// Иконка вопроса
        );

        if (result == JOptionPane.YES_OPTION) {
            saveWindowStateAndExit(); // Сохраняем состояния и завершаем программу
        }
    }

    private void saveWindowStateAndExit()
    {
        if (logWindow != null)
        {
            saveWindowState(logWindow, "LogWindow");
        }
        if (gameWindow != null)
        {
            saveWindowState(gameWindow, "GameWindow");
        }
        configManager.SaveToFile();
        System.exit(0);
    }

    private void saveWindowState(JInternalFrame frame, String windowName)
    {
        try
        {
            // определяем текущее состояние окна: 0 - нормальное, 1 - развёрнутое, 2 - свёрнутое
            int state = 0; // normal
            if (frame.isMaximum())
            {
                state = 1; // maximized
            }
            else if (frame.isIcon())
            {
                state = 2; // iconified (minimized)
            }

            // создаём объект состояния с текущими параметрами окна
            WindowConfigManager.WindowState windowState = new WindowConfigManager.WindowState(
                    frame.getX(),
                    frame.getY(),
                    frame.getWidth(),
                    frame.getHeight(),
                    state,
                    frame.isClosed()
            );

            // сохраняем в менеджер конфигурации
            configManager.SaveState(windowName, windowState);
        }
        catch (Exception e)
        {
            System.err.println("Error saving state for " + windowName + ": " + e.getMessage());
        }
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className); // Устанавливаем новый LookAndFeel
            SwingUtilities.updateComponentTreeUI(this);// Обновляем все компоненты.
        }
        catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}