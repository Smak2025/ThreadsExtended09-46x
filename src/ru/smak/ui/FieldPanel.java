package ru.smak.ui;
import ru.smak.Circle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FieldPanel extends JPanel {
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> removingTask = null;
    private ScheduledFuture<?> addingTask = null;
    private final List<Circle> circles = new ArrayList<>();
    private final List<Circle> tmpCircles = new ArrayList<>();
    public FieldPanel() {
        setBackground(Color.WHITE);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                circles.forEach(c -> {
                    c.setContainerHeight(getHeight());
                    c.setContainerWidth(getWidth());
                });
            }
        });
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Add/Remover-Thread");
            t.setDaemon(true);
            return t;
        });
    }

    public void addAllCircles(List<Circle> newCircles){
        stopClearing();
        if (addingTask == null) {
            synchronized (circles) {
                circles.clear();
            }
            tmpCircles.addAll(newCircles);
            addingTask = scheduler.scheduleAtFixedRate(this::addNextCircle, 0, 100, TimeUnit.MILLISECONDS);
        }
    }

    public void clearCircles(){
        stopAdding();
        if (removingTask == null) {
            removingTask = scheduler.scheduleAtFixedRate(this::removeNextCircle, 0, 100, TimeUnit.MILLISECONDS);
        }
    }

    private void stopClearing(){
        if (removingTask != null) {
            removingTask.cancel(true);
            removingTask = null;
        }
    }

    private void stopAdding(){
        if (addingTask != null){
            addingTask.cancel(true);
            addingTask = null;
        }
    }

    private void removeNextCircle(){
        synchronized (circles) {
            if (!circles.isEmpty()) {
                circles.removeFirst();
            } else {
                stopClearing();
            }
        }
        SwingUtilities.invokeLater(this::repaint);
    }

    private void addNextCircle(){
        synchronized (circles){
            if (tmpCircles != null && !tmpCircles.isEmpty()){
                circles.add(tmpCircles.getFirst());
                tmpCircles.removeFirst();
            } else {
                addingTask.cancel(true);
                addingTask = null;
            }
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