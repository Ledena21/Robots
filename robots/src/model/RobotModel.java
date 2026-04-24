package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель робота. Содержит всю логику движения и состояние.
 * Слушатели подписываются через addListener()
 * При изменении состояния вызывается notifyListeners()
 */

public class RobotModel {
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private static final double maxVelocity = 0.1;          // Максимальная линейная скорость
    private static final double maxAngularVelocity = 0.001; // Максимальная угловая скорость

    private final List<RobotModelListener> listeners = new ArrayList<>(); // Список слушателей

    /**
     * Интерфейс слушателя изменений модели.
     * Окна реализуют этот интерфейс для получения уведомлений.
     */
    public interface RobotModelListener {
        void onRobotPositionChanged(double x, double y, double direction);
        void onTargetPositionChanged(int x, int y);
    }

    /**
     * Подписка на обновления.
     * Окно вызывает этот метод, чтобы получать уведомления.
     */
    public void addListener(RobotModelListener listener) {
        synchronized(listeners) {
            listeners.add(listener);
        }
    }

    // Отписка от обновлений
    public void removeListener(RobotModelListener listener) {
        synchronized(listeners) {
            listeners.remove(listener);
        }
    }

    //  Геттеры
    public double getRobotPositionX() { return m_robotPositionX; }
    public double getRobotPositionY() { return m_robotPositionY; }
    public double getRobotDirection() { return m_robotDirection; }
    public int getTargetPositionX() { return m_targetPositionX; }
    public int getTargetPositionY() { return m_targetPositionY; }

    //  Сеттеры
    public void setTargetPosition(int x, int y) {
        m_targetPositionX = x;
        m_targetPositionY = y;
        notifyTargetListeners();
    }

    public void setTargetPosition(double x, double y) {
        m_targetPositionX = (int)x;
        m_targetPositionY = (int)y;
        notifyTargetListeners();
    }

    /**
     * Обновляет позицию робота на один шаг.
     * Вызывается по таймеру из GameVisualizer.
     */
    public void updatePosition() {
        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);

        if (distance < 0.5) {
            return; // достигли цели
        }

        double velocity = maxVelocity;
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY,
                m_targetPositionX, m_targetPositionY);

        double angularVelocity = calculateAngularVelocity(angleToTarget);
        moveRobot(velocity, angularVelocity, 10);
        notifyListeners();
    }


    private double calculateAngularVelocity(double angleToTarget) {
        // Вычисляем разность углов
        double diff = angleToTarget - m_robotDirection;

        // Нормализуем разность в диапазон [-π, π]
        while (diff < -Math.PI) diff += 2 * Math.PI;
        while (diff > Math.PI) diff -= 2 * Math.PI;

        // Если разница очень маленькая - не поворачиваем
        if (Math.abs(diff) < 0.01) {
            return 0;
        }

        // Поворачиваем в сторону, где разница положительная или отрицательная
        return diff > 0 ? maxAngularVelocity : -maxAngularVelocity;
    }

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
     * Уведомляет всех зарегистрированных слушателей об изменении позиции
     */
    private void notifyListeners() {
        // создаём копию списка для безопасного итерирования
        List<RobotModelListener> listenersCopy;
        synchronized(listeners) {
            listenersCopy = new ArrayList<>(listeners);
        }
        for (RobotModelListener listener : listenersCopy) {
            listener.onRobotPositionChanged(m_robotPositionX, m_robotPositionY, m_robotDirection);
        }
    }

    private void notifyTargetListeners() {
        List<RobotModelListener> listenersCopy;
        synchronized(listeners) {
            listenersCopy = new ArrayList<>(listeners);
        }
        for (RobotModelListener listener : listenersCopy) {
            listener.onTargetPositionChanged(m_targetPositionX, m_targetPositionY);
        }
    }

    // вычисляет евклидово расстояние между двумя точками (корень из суммы квадрата координат)
    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    // находит угол, под которым робот должен двигаться к цели
    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    // ограничивает значение заданными пределами (min и max).
    private static double applyLimits(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    // метод приводит угол к нормализованному виду — значению в диапазоне от 0 до 2π (0 до 360 градусов)
    private static double asNormalizedRadians(double angle) {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }
}