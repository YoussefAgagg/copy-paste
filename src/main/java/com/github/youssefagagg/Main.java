package com.github.youssefagagg;


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class Main {
    private CopyPasteFrame c;

    public static void main(String[] args) {

        new Main();
    }

    public Main() {
        EventQueue.invokeLater(new Runnable() {
            // @Override
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
                frame.addWindowListener(new WindowListener() {

                    @Override
                    public void windowOpened(WindowEvent e) {

                    }

                    @Override
                    public void windowIconified(WindowEvent e) {

                    }

                    @Override
                    public void windowDeiconified(WindowEvent e) {

                    }

                    @Override
                    public void windowDeactivated(WindowEvent e) {

                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
//
//						File file=c.writeFile();
//						if(file!=null) {
//							try {
//							c.writePaint(file.getPath());
//							} catch (IOException ex) {
//								ex.printStackTrace();
//							}
//							}
                        //c.writeTXT(new File("C:\\Users\\dell\\Documents\\sub.txt"));
                        //frame.setVisible(false);
                        //System.exit(0); discuss

                    }

                    @Override
                    public void windowClosed(WindowEvent e) {

                    }

                    @Override
                    public void windowActivated(WindowEvent e) {

                    }
                });
            }
        });
    }
}