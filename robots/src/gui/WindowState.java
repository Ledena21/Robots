package gui;

public class WindowState {
    private final int x, y, width, height, state;
    private final boolean closed;

    public enum Type {
        NORMAL(0), // обычный режим
        MAXIMIZED(1), // во весь экран
        ICONIFIED(2); // свернуто в значок

        private final int num; // число

        Type(int num) { // конструктор
            this.num = num;
        }

        public int getNum() {
            return num; // возвращает число
        }

        public static Type fromNum(int num) {
            for (Type type : values()) {
                if (type.num == num) {
                    return type;
                }
            }
            return NORMAL; // значение по умолчанию
        }
    }

    public WindowState(int x, int y, int width, int height, int state, boolean isClosed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.state = state;
        this.closed = isClosed;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getState() { return state; }

    // Возвращает тип состояния окна как enum
    public Type getType() {
        return Type.fromNum(state);
    }

    public boolean isClosed() { return closed; }
}