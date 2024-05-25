package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class GameStatusWindow extends JFrame {
    private JLabel statusLabel;
    private JButton okButton;
    private GameStatusCallback callback;

    public interface GameStatusCallback {
        void onOKClicked();
    }

    public GameStatusWindow(String status, GameStatusCallback callback) {
        super("Game Status");
        this.callback = callback;
        statusLabel = new JLabel(status);
        okButton = new JButton("OK");

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                callback.onOKClicked(); // 通知游戏继续
                dispose(); // 关闭窗口
            }
        });

        JPanel panel = new JPanel();
        panel.add(statusLabel);
        panel.add(okButton);
        add(panel);

        setSize(230, 120);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // 重写processWindowEvent方法，捕获按下P、空格或R键
    @Override
    protected void processKeyEvent(KeyEvent e) {
        super.processKeyEvent(e);
        if (e.getKeyCode() == KeyEvent.VK_P || e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_R) {
            callback.onOKClicked();
            dispose();
        }
    }
}
