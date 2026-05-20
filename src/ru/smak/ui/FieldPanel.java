package ru.smak.ui;
import ru.smak.Circle;

import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FieldPanel extends JPanel {
    private final List<Circle> circles = new ArrayList<>();
    public FieldPanel() {
        setBackground(Color.WHITE);
    }

    public void addAllCircles(List<Circle> circle){
        circles.addAll(circle);
    }

    public void clearCircles(){
        circles.clear();
    }

    public void removeNextCircle(){
        if (!circles.isEmpty()){
            circles.removeFirst();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        // Сглаживание для красивой картинки
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Блокируем список на время отрисовки, чтобы фоновый поток не изменил координаты mid-draw
        synchronized (circles) {
            for (Circle circle : circles) {
                circle.draw(g2d);
            }
        }
    }
}