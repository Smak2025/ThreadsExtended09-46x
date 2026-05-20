package ru.smak;
import java.awt.Color;
import java.awt.Graphics2D;

public class Circle {
    private double x, y;      // Текущая позиция
    private double dx, dy;    // Вектор скорости
    private final Color color;
    private final int radius;
    private int containerWidth, containerHeight;

    /**
     * Создает объект-кружок с заданными характеристиками
     * @param x абсцисса центра кружка
     * @param y ордината центра кружка
     * @param dx    скорость кружка по горизонтали
     * @param dy    скорость кружка по вертикали
     * @param color цвет кружка
     * @param radius    радиус кружка
     * @param containerWidth    ширина области, в которой может перемещаться кружок
     * @param containerHeight   высота области, в которой может перемещаться кружок
     */
    public Circle(
            double x,
            double y,
            double dx,
            double dy,
            Color color,
            int radius,
            int containerWidth,
            int containerHeight
    ) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.color = color;
        this.radius = radius;
        this.containerWidth = containerWidth;
        this.containerHeight = containerHeight;
    }

    /**
     * Выполняет алгоритм сдвига кружка на один шаг
     * в соответствии со скоростью по горизонтальной и вертикальной составляющим,
     * а также с учтом препятствий в виде границ видимой области
     */
    public void move() {
        x += dx;
        y += dy;

        // Отскок от стен
        if (x - radius < 0 || x + radius > containerWidth) dx = -dx;
        if (y - radius < 0 || y + radius > containerHeight) dy = -dy;

        // Фиксация выхода за границы (защита от "застревания")
        x = Math.clamp(x, radius, containerWidth - radius);
        y = Math.clamp(y, radius, containerHeight - radius);
    }

    /** Отрисовка кружка */
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillOval(
            (int)(x - radius),
            (int)(y - radius),
            radius * 2,
            radius * 2
        );
    }

    public int getContainerWidth() {
        return containerWidth;
    }

    public void setContainerWidth(int containerWidth) {
        this.containerWidth = containerWidth;
    }

    public int getContainerHeight() {
        return containerHeight;
    }

    public void setContainerHeight(int containerHeight) {
        this.containerHeight = containerHeight;
    }
}