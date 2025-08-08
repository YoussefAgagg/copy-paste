package com.github.youssefagagg;


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    private CopyPasteFrame c;

    public static void main(String[] args) {

        new Main();
    }

    public Main() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                    try {
                        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                             UnsupportedLookAndFeelException ex) {
                    }

                final JFrame frame = new JFrame("Copy-Paste");
                c = new CopyPasteFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(c);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        // Application will close automatically with EXIT_ON_CLOSE
                    }
                });
            }
        });
    }
}