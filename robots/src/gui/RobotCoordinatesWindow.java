package gui;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import model.RobotModel;
/**
 * Отображает данные модели, подписывается на её обновления
 выводит точные координаты робота в реальном времени
 */
public class RobotCoordinatesWindow extends JInternalFrame implements RobotModel.RobotModelListener, Saveable {
    private RobotModel m_model;
    private TextArea m_coordinatesDisplay;

    public RobotCoordinatesWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);
        m_model = model;
        // подписываемся на обновления модели, при каждом движении робота будет вызываться onRobotPositionChanged()
        m_model.addListener(this);

        m_coordinatesDisplay = new TextArea("");
        m_coordinatesDisplay.setEditable(false);
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
    public void onTargetPositionChanged(int x, int y) {}

    private void updateCoordinates(double x, double y, double direction) {
        EventQueue.invokeLater(this::updateDisplay);
    }

    //Возвращает уникальное строковое имя окна.
    @Override
    public String getWindowName() {
        return "CoordinatesWindow";
    }

    //Собирает текущее состояние окна в один объект WindowState и возвращает его.
    @Override
    public WindowState getWindowState() {
        int state = isMaximum() ? 1 : (isIcon() ? 2 : 0);
        return new WindowState(getX(), getY(), getWidth(), getHeight(), state, isClosed());
    }
}