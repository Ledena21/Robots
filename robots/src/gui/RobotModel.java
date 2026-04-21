package model;

import java.util.ArrayList;
import java.util.List;

/**
 * RobotModel модель робота, отвечающая за его физическое поведение.
 * Хранит текущую позицию x, y, направление движения
 * Уведомляет зарегистрированных слушателей об изменении позиции через интерфейс RobotModelListener.
 * m_robotPositionX, m_robotPositionY — текущие координаты робота.
 * m_robotDirection — текущее направление робота в радианах
 * maxVelocity — максимальная линейная скорость робота.
 * maxAngularVelocity — максимальная угловая скорость поворота.
 * listeners — список слушателей, получающих уведомления об изменении позиции.
 */
public class RobotModel {
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;
    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;

    private final List<RobotModelListener> listeners = new ArrayList<>();

    /**
     * Интерфейс для слушателей изменений позиции робота.
     * Классы, реализующие этот интерфейс, могут отслеживать движение робота
     * и реагировать на обновление его координат и направления.
     */
    public interface RobotModelListener {
        void changePosition(double x, double y, double direction);
    }

    // добавляем слушателя в список наблюдателей.
    public void addListener(RobotModelListener listener) {
        synchronized(listeners) {
            listeners.add(listener);
        }
    }

    // удаляем слушателя из списка пользователей
    public void removeListener(RobotModelListener listener) {
        synchronized(listeners) {
            listeners.remove(listener);
        }
    }

    public double getRobotPositionX() { return m_robotPositionX; }
    public double getRobotPositionY() { return m_robotPositionY; }
    public double getRobotDirection() { return m_robotDirection; }
    public int getTargetPositionX() { return m_targetPositionX; }
    public int getTargetPositionY() { return m_targetPositionY; }

    /**
     * Устанавливает целочисленные целевые координаты для робота
     */
    public void setTargetPosition(int x, int y) {
        m_targetPositionX = x;
        m_targetPositionY = y;
    }

    /**
     * Обновляем позицию робота.
     * Вычисляем расстояние до цели. У робота максимальная скорость maxVelocity.
     * angleTo возвращает угол от текущей позиции робота к цели. Находим кратчайший путь, как повернуться.
     * Говорим, насколько далеко проехать за шаг, насколько повернуться и длительность шага
     */
    public void updatePosition() {
        double distance = distance(m_targetPositionX, m_targetPositionY, m_robotPositionX, m_robotPositionY);

        if (distance < 0.5) {
            return;
        }

        double velocity = maxVelocity;
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        double angularVelocity = calculateAngularVelocity(angleToTarget);
        moveRobot(velocity, angularVelocity, 10);
        notifyListeners();
    }

    private double calculateAngularVelocity(double angleToTarget) {
        double diff = angleToTarget - m_robotDirection;
        while (diff < -Math.PI) diff += 2 * Math.PI;
        while (diff > Math.PI) diff -= 2 * Math.PI;

        if (Math.abs(diff) < 0.01) {
            return 0;
        }

        return diff > 0 ? maxAngularVelocity : -maxAngularVelocity;
    }

    /**
     * Выполняет перемещение робота на один шаг с заданными скоростями.
     */
    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);

        double newX, newY;

        if (Math.abs(angularVelocity) < 0.0001) {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        } else {
            newX = m_robotPositionX + velocity / angularVelocity *
                    (Math.sin(m_robotDirection + angularVelocity * duration) - Math.sin(m_robotDirection));
            newY = m_robotPositionY - velocity / angularVelocity *
                    (Math.cos(m_robotDirection + angularVelocity * duration) - Math.cos(m_robotDirection));
        }

        if (!Double.isFinite(newX) || !Double.isFinite(newY)) {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }

        m_robotPositionX = newX;
        m_robotPositionY = newY;
        m_robotDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
    }

    /**
     * Уведомляет всех зарегистрированных слушателей об изменении позиции.
     */
    private void notifyListeners() {
        List<RobotModelListener> listenersCopy;
        synchronized(listeners) {
            listenersCopy = new ArrayList<>(listeners);
        }
        for (RobotModelListener listener : listenersCopy) {
            listener.changePosition(m_robotPositionX, m_robotPositionY, m_robotDirection);
        }
    }

    /**
     * Вычисляет евклидово расстояние между двумя точками
     */
    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    /**
     * Вычисляет угол направления от одной точки к другой в радианах.
     */
    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    /**
     * Ограничивает значение в заданных пределах [min, max].
     */
    private static double applyLimits(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Нормализует угол в радианах в диапазон [0, 2π).
     */
    private static double asNormalizedRadians(double angle) {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }
}