import ru.smak.Animator;
import ru.smak.Circle;
import ru.smak.ui.FieldPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

void main(String[] args) {
    // Swing-инициализация ВСЕГДА на EDT
    SwingUtilities.invokeLater(this::createAndShowGUI);
}

private void createAndShowGUI() {
    JFrame frame = new JFrame("Modern Java Concurrency: Animator");
    frame.setMinimumSize(new Dimension(300, 250));
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.setSize(800, 600);
    frame.setLocationRelativeTo(null);

    FieldPanel panel = new FieldPanel();
    frame.add(panel, BorderLayout.CENTER);

    // Кнопки управления
    JPanel controls = new JPanel();
    JButton startBtn = new JButton("▶ Start");
    JButton stopBtn = new JButton("⏹ Stop");
    controls.add(startBtn);
    controls.add(stopBtn);
    frame.add(controls, BorderLayout.SOUTH);
    frame.setVisible(true);

    // Генерация шариков
    List<Circle> circles = new ArrayList<>();
    Random rand = new Random();
    for (int i = 0; i < 30; i++) {
        circles.add(new Circle(
                rand.nextDouble(100, 700),
                rand.nextDouble(100, 500),
                rand.nextDouble(2, 6) * (rand.nextBoolean() ? 1 : -1),
                rand.nextDouble(2, 6) * (rand.nextBoolean() ? 1 : -1),
                new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)),
                rand.nextInt(10, 25),
                panel.getWidth(),
                panel.getHeight()
                )
        );
    }
    // Контроллер анимации
    Animator animator = new Animator(circles);
    animator.addFrameListener(()->{
        SwingUtilities.invokeLater(panel::repaint);
    });

    startBtn.addActionListener(e -> {
        panel.addAllCircles(circles);
        animator.start();
    });
    stopBtn.addActionListener(e -> {
        animator.stop();
        panel.clearCircles();
    });
    // Корректное завершение при закрытии окна
    frame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            animator.stop(); // Останавливаем пул перед выходом
            frame.dispose();
        }
    });
}