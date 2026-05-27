package gui;

public record WindowState(int x, int y, int width, int height, WindowStateType state, boolean isClosed) {
}