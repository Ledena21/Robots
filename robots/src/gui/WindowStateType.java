package gui;

/**
 * Типы состояний окна: нормальное, развёрнутое, свёрнутое.
 * Вынесен в отдельный класс для удобства переиспользования.
 */
public enum WindowStateType {
    NORMAL(0),      // обычный режим
    MAXIMIZED(1),   // во весь экран
    ICONIFIED(2);   // свёрнуто в значок

    private final int num;

    WindowStateType(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public static WindowStateType fromNum(int num) {
        for (WindowStateType type : values()) {
            if (type.num == num) {
                return type;
            }
        }
        return NORMAL; // значение по умолчанию
    }
}