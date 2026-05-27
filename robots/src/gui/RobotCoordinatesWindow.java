package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import model.RobotModel;

/**
 * Окно отображения текущих координат робота и его цели в реальном времени.
 * Реализует паттерн Observer (слушатель) для мгновенного обновления информации при изменении модели.
 */
public class RobotCoordinatesWindow extends JInternalFrame implements RobotModel.RobotModelListener, Saveable {
    private final RobotModel m_model;
    private final JTextArea m_coordinatesDisplay;

    /**
     * Создает новое окно координат робота на основе предоставленной модели.
     * Возвращает источник данных о положении робота.
     */
    public RobotCoordinatesWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);
        m_model = model;
        m_model.addListener(this);

        m_coordinatesDisplay = new JTextArea("");
        m_coordinatesDisplay.setEditable(false);
        m_coordinatesDisplay.setFocusable(false);
        JScrollPane scrollPane = new JScrollPane(m_coordinatesDisplay);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateDisplay();
    }

    /**
     * Извлекает из модели актуальные координаты и перерисовывает текстовое поле окна.
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

    /**
     * Перехватывает событие изменения позиции робота.
     */
    @Override
    public void onRobotPositionChanged(double x, double y, double direction) {
        EventQueue.invokeLater(this::updateDisplay);
    }

    /**
     * Перехватывает событие перемещения целевой точки.
     */
    @Override
    public void onTargetPositionChanged(int x, int y) {
        EventQueue.invokeLater(this::updateDisplay);
    }

    /**
     * Возвращает текстовое имя окна.
     */
    @Override
    public String getWindowName() {
        return "CoordinatesWindow";
    }
}