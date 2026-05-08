package gui;

import javax.swing.JInternalFrame;

public interface Saveable {
    String getWindowName();

    /**
     * Получение состояния окна.
     * Если класс реализует Saveable и является JInternalFrame,
     * метод автоматически соберёт состояние через WindowStateManager.
     */
    default WindowState getWindowState() {
        if (this instanceof JInternalFrame) {
            return WindowStateManager.createWindowState((JInternalFrame) this);
        }
        return null;
    }
}