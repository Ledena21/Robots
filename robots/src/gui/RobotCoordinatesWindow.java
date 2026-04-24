package gui;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import model.RobotModel;
/**
 * Отображает данные модели, подписывается на обновления модели
 * мы видим точные координаты робота в реальном времени
 */
public class RobotCoordinatesWindow extends JInternalFrame implements RobotModel.RobotModelListener, Saveable {
    private RobotModel m_model;
    private TextArea m_coordinatesDisplay;//текстовое поле с координатами

    public RobotCoordinatesWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);
        m_model = model;
        // подписываемся на обновления модели, при каждом движении робота будет вызываться onRobotPositionChanged()
        m_model.addListener(this);
        // Создаём текстовое поле для отображения
        m_coordinatesDisplay = new TextArea("");
        m_coordinatesDisplay.setEditable(false);// Только для чтения
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_coordinatesDisplay, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateDisplay();
    }

    /**
     * Обновляет текстовое отображение координат.
     * Читает текущие данные из модели.
     */
    private void updateDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append(" ПОЛОЖЕНИЕ РОБОТА\n");
        sb.append("Позиция X: ").append(String.format("%.2f", m_model.getRobotPositionX())).append("\n");
        sb.append("Позиция Y: ").append(String.format("%.2f", m_model.getRobotPositionY())).append("\n");
        sb.append("Направление: ").append(String.format("%.4f рад", m_model.getRobotDirection())).append("\n");
        sb.append("Направление: ").append(String.format("%.1f град", Math.toDegrees(m_model.getRobotDirection()))).append("\n");
        sb.append("\nЦЕЛЬ\n");
        sb.append("Цель X: ").append(m_model.getTargetPositionX()).append("\n");
        sb.append("Цель Y: ").append(m_model.getTargetPositionY()).append("\n");
        m_coordinatesDisplay.setText(sb.toString());
    }

    // вызываем и реагируем на событие (обновляем координаты)
    @Override
    public void onRobotPositionChanged(double x, double y, double direction) {
        updateCoordinates(x, y, direction);
    }

    @Override
    public void onTargetPositionChanged(int x, int y) {
        // окно координат может игнорировать изменение цели или тоже как-то отображать
    }

    private void updateCoordinates(double x, double y, double direction) {
        EventQueue.invokeLater(this::updateDisplay);
    }

    // возвращает имя окна.
    @Override
    public String getWindowName() {
        return "CoordinatesWindow";
    }

    //Собирает текущее состояние окна в один объект WindowState и возвращает его.
    @Override
    public WindowState getWindowState() {
        // Используем enum для определения состояния окна
        WindowState.Type type = isMaximum() ? WindowState.Type.MAXIMIZED : (isIcon() ? WindowState.Type.ICONIFIED : WindowState.Type.NORMAL);
        return new WindowState(getX(), getY(), getWidth(), getHeight(), type.getNum(), isClosed());
    }
}