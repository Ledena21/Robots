package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import model.RobotModel;

/**
 * model - модель робота, источник данных о позиции и цели
 * coordinatesDisplay - текстовое поле для отображения координат (только чтение).
 */
public class RobotCoordinatesWindow extends JInternalFrame implements RobotModel.RobotModelListener, Saveable {
    private RobotModel model;
    private TextArea coordinatesDisplay;

    public RobotCoordinatesWindow(RobotModel model) { // создаём окно координат, регистрируемся как слушатель модели
        super("Координаты робота", true, true, true, true);
        this.model = model;
        this.model.addListener(this);

        this.coordinatesDisplay = new TextArea("");
        this.coordinatesDisplay.setEditable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(this.coordinatesDisplay, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateDisplay();
    }

    /** Метод, который обновляет текст в окне координат.
     * Формирует строку с текущей позицией робота, направлением, целевыми координатами
     * и разностью между текущей и целевой позицией. Числа форматируются до 2-4 знаков.
     * Результат записывается в coordinatesDisplay.
     */
    private void updateDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("Позиция X: ").append(String.format("%.2f", this.model.getRobotPositionX())).append("\n");
        sb.append("Позиция Y: ").append(String.format("%.2f", this.model.getRobotPositionY())).append("\n");
        sb.append("Направление: ").append(String.format("%.4f рад", this.model.getRobotDirection())).append("\n");
        sb.append("Цель X: ").append(this.model.getTargetPositionX()).append("\n");
        sb.append("Цель Y: ").append(this.model.getTargetPositionY()).append("\n\n");

        double dx = this.model.getTargetPositionX() - this.model.getRobotPositionX();
        double dy = this.model.getTargetPositionY() - this.model.getRobotPositionY();
        sb.append("Разность X: ").append(String.format("%.2f", dx)).append("\n");
        sb.append("Разность Y: ").append(String.format("%.2f", dy)).append("\n");

        this.coordinatesDisplay.setText(sb.toString());
    }

    @Override
    public void onRobotPositionChanged(double x, double y, double direction) { // вызывается моделью при изменении позиции робота
        EventQueue.invokeLater(this::updateDisplay); // обновляем интерфейс
    }

    @Override
    public String getWindowName() { // возвращаем имя окна для конфигурации
        return "CoordinatesWindow";
    }

    @Override
    public WindowState getWindowState() { // собираем текущее состояние окна для сохранения
        int state = isMaximum() ? 1 : (isIcon() ? 2 : 0);
        return new WindowState(getX(), getY(), getWidth(), getHeight(), state, isClosed());
    }
}