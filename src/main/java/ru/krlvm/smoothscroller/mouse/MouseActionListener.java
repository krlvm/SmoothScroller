package ru.krlvm.smoothscroller.mouse;

public interface MouseActionListener {
    void mouseClick();
    void mouseWheel(boolean direction); //true=down false=up
}
