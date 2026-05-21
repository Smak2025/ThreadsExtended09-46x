package ru.smak;

import ru.smak.ui.FieldPanel;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Animator {
    private final ScheduledExecutorService scheduler;
    private final List<Runnable> listeners = new ArrayList<>();
    private final List<Circle> circles;
    private volatile boolean running = false;

    // Ссылка на запланированную задачу для отмены
    private ScheduledFuture<?> animationTask;

    public Animator(List<Circle> circles) {
        this.circles = circles;

        // Пул создаётся ОДИН раз и живёт до закрытия приложения
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Animator-Thread");
            t.setDaemon(true);
            return t;
        });
    }

    public void start() {
        if (running) return;
        running = true;
        // Сохраняем Future, чтобы позже отменить именно эту задачу
        animationTask = scheduler.scheduleAtFixedRate(
                this::animationTick,
                0,
                16,
                TimeUnit.MILLISECONDS
        );
    }

    public void stop() {
        if (!running) return;
        running = false;
        // Отменяем задачу, но НЕ закрываем пул!
        // false = дождёмся завершения текущего тика (безопасно для repaint)
        if (animationTask != null) {
            animationTask.cancel(false);
        }
    }

    // Вызывается ТОЛЬКО при полном завершении приложения
    public void shutdown() {
        stop(); // На случай если забыли нажать Stop
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            scheduler.shutdownNow();
        }
    }

    private void animationTick() {
        circles.forEach(Circle::move);
        listeners.forEach(Runnable::run);
        //SwingUtilities.invokeLater(panel::repaint);
    }

    public void addFrameListener(Runnable l){
        listeners.add(l);
    }
}