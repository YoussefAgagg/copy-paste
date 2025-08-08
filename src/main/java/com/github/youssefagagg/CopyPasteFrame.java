package com.github.youssefagagg;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class CopyPasteFrame extends JPanel implements Serializable {

    private static final long serialVersionUID = 999999999999999999l;



    DefaultListModel<String> d;
    DefaultListModel<String> pinnedModel;
    private JList<String> textl;
    private JList<String> pinnedList;
    private JButton copy, remove, removeall, pin, unpin;
    private String flag;
    private static final String PINNED_DIR = System.getProperty("user.home") + File.separator + "copy-past" + File.separator + ".pinedcopies";
    private static final String PINNED_FILE = PINNED_DIR + File.separator + "pinned_messages.txt";

    public CopyPasteFrame() {
        d = new DefaultListModel<String>();
        pinnedModel = new DefaultListModel<String>();
        
        // Load saved pinned messages on startup
        loadPinnedMessages();

        textl = new JList<String>(d);
        pinnedList = new JList<String>(pinnedModel);
        JPanel p = new JPanel(new FlowLayout());
        setLayout(new BorderLayout());
        
        // Setup pinned list
        pinnedList.setFixedCellHeight(30);
        pinnedList.setFixedCellWidth(380);
        pinnedList.setBackground(new Color(40, 40, 40));
        pinnedList.setForeground(Color.YELLOW);
        pinnedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pinnedList.setCellRenderer(getRenderer());
        textl.setAutoscrolls(true);
        textl.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        copy = new JButton("copy");
        copy.setBackground(Color.WHITE);
        remove = new JButton("remove");
        remove.setBackground(Color.WHITE);
        removeall = new JButton("removeAll");
        removeall.setBackground(Color.WHITE);
        pin = new JButton("pin");
        pin.setBackground(Color.YELLOW);
        pin.setForeground(Color.BLACK);
        unpin = new JButton("unpin");
        unpin.setBackground(Color.ORANGE);
        unpin.setForeground(Color.BLACK);
        p.add(copy);
        p.add(remove);
        p.add(removeall);
        p.add(pin);
        p.add(unpin);
        copy.setFocusable(false);
        remove.setFocusable(false);
        remove.setToolTipText("remove selection copy from the list");
        removeall.setFocusable(false);
        removeall.setToolTipText("remove all copies");
        pin.setFocusable(false);
        pin.setToolTipText("pin selected text to top");
        unpin.setFocusable(false);
        unpin.setToolTipText("unpin selected text from top");
        textl.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                textl.setSelectedIndex(textl.getSelectedIndex());
                // Clear pinned list selection when regular list is selected
                if (textl.getSelectedIndex() != -1) {
                    pinnedList.clearSelection();
                }
            }
        });
        
        // Add selection listener for pinned list
        pinnedList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Clear regular list selection when pinned list is selected
                if (pinnedList.getSelectedIndex() != -1) {
                    textl.clearSelection();
                }
            }
        });
        removeall.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                d.removeAllElements();

            }
        });

        textl.setFixedCellHeight(50);
        textl.setFixedCellWidth(380);
        textl.setCellRenderer(getRenderer());
        p.setBackground(Color.BLACK);
        
        // Create pinned text panel at the top
        JPanel pinnedPanel = new JPanel(new BorderLayout());
        pinnedPanel.setBackground(Color.BLACK);
        JLabel pinnedLabel = new JLabel("Pinned Text:");
        pinnedLabel.setForeground(Color.YELLOW);
        pinnedLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pinnedPanel.add(pinnedLabel, BorderLayout.NORTH);
        
        JScrollPane pinnedScrollPane = new JScrollPane(pinnedList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pinnedScrollPane.setPreferredSize(new Dimension(400, 80));
        pinnedScrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(50, 80, 100)));
        pinnedPanel.add(pinnedScrollPane, BorderLayout.CENTER);
        
        // Create main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(p, BorderLayout.NORTH);
        
        JScrollPane sPane = new JScrollPane(textl, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        textl.setBackground(Color.BLACK);
        textl.setForeground(Color.WHITE);
        sPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(50, 80, 100)));
        mainPanel.add(sPane, BorderLayout.CENTER);
        
        // Add panels to main frame
        add(pinnedPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);


        Thread t = new Thread(new ContentsMonitor());
        t.setDaemon(true);
        t.start();
        
        // Add shutdown hook to save pinned messages when application closes
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            savePinnedMessages();
        }));
        textl.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C && textl.getSelectedIndex() != -1) {
                    setSysClipboardText(d.get(textl.getSelectedIndex()));
                    flag = d.elementAt(textl.getSelectedIndex());
                }
            }
        });
        
        // Add keyboard listener for pinned list
        pinnedList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C && pinnedList.getSelectedIndex() != -1) {
                    setSysClipboardText(pinnedModel.get(pinnedList.getSelectedIndex()));
                    flag = pinnedModel.elementAt(pinnedList.getSelectedIndex());
                }
            }
        });
        remove.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                        if (textl.getSelectedIndex() != -1)
                            d.remove(textl.getSelectedIndex());

                    }
                }
        );
        copy.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Check if regular list item is selected
                        if (textl.getSelectedIndex() != -1) {
                            setSysClipboardText(d.get(textl.getSelectedIndex()));
                            flag = d.elementAt(textl.getSelectedIndex());
                        }
                        // Check if pinned list item is selected
                        else if (pinnedList.getSelectedIndex() != -1) {
                            setSysClipboardText(pinnedModel.get(pinnedList.getSelectedIndex()));
                            flag = pinnedModel.elementAt(pinnedList.getSelectedIndex());
                        }
                    }
                }
        );
        
        // Pin functionality - move selected text from clipboard list to pinned list
        pin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = textl.getSelectedIndex();
                if (selectedIndex != -1) {
                    String selectedText = d.get(selectedIndex);
                    // Check if text is not already pinned
                    boolean alreadyPinned = false;
                    for (int i = 0; i < pinnedModel.getSize(); i++) {
                        if (pinnedModel.get(i).equals(selectedText)) {
                            alreadyPinned = true;
                            break;
                        }
                    }
                    if (!alreadyPinned) {
                        pinnedModel.addElement(selectedText);
                        d.remove(selectedIndex);
                        // Save pinned messages to persistent storage
                        savePinnedMessages();
                    }
                }
            }
        });
        
        // Unpin functionality - move selected text from pinned list to clipboard list
        unpin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = pinnedList.getSelectedIndex();
                if (selectedIndex != -1) {
                    String selectedText = pinnedModel.get(selectedIndex);
                    pinnedModel.remove(selectedIndex);
                    d.add(0, selectedText); // Add to top of clipboard list
                    // Save pinned messages to persistent storage
                    savePinnedMessages();
                }
            }
        });
    }

    public int get() {
        return d.getSize();
    }

    /**
     * Save pinned messages to persistent storage
     */
    private void savePinnedMessages() {
        try {
            // Create directory if it doesn't exist
            Path dirPath = Paths.get(PINNED_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // Build content with delimiter-separated messages
            StringBuilder content = new StringBuilder();
            for (int i = 0; i < pinnedModel.getSize(); i++) {
                String message = pinnedModel.get(i);
                // Escape newlines to preserve multi-line text
                String escapedMessage = message.replace("\n", "\\n").replace("\r", "\\r");
                content.append(escapedMessage);
                if (i < pinnedModel.getSize() - 1) {
                    content.append("\n###PINNED_MESSAGE_SEPARATOR###\n");
                }
            }
            
            // Write to file as a single string
            Files.write(Paths.get(PINNED_FILE), content.toString().getBytes());
        } catch (IOException e) {
            System.err.println("Error saving pinned messages: " + e.getMessage());
        }
    }

    /**
     * Load pinned messages from persistent storage
     */
    private void loadPinnedMessages() {
        try {
            Path filePath = Paths.get(PINNED_FILE);
            if (Files.exists(filePath)) {
                // Read entire file content
                String content = new String(Files.readAllBytes(filePath));
                
                if (content.trim().isEmpty()) {
                    return;
                }
                
                // Split by delimiter to get individual messages
                String[] messages = content.split("\n###PINNED_MESSAGE_SEPARATOR###\n");
                
                for (String message : messages) {
                    if (message != null && !message.trim().isEmpty()) {
                        // Restore escaped newlines and carriage returns
                        String restoredMessage = message.replace("\\n", "\n").replace("\\r", "\r");
                        pinnedModel.addElement(restoredMessage);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading pinned messages: " + e.getMessage());
        }
    }


    public void setSysClipboardText(String writeMe) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(writeMe);
        clip.setContents(tText, null);
    }

    private ListCellRenderer<? super String> getRenderer() {
        return new DefaultListCellRenderer() {

            private static final long serialVersionUID = 999999999999999999l;

            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {

                JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                listCellRendererComponent.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 80, 100)));
                return listCellRendererComponent;
            }
        };
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 400);
    }

    protected String getClipboardContents() {
        String text = null;
        Clipboard clipboard;
        try {
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        try {
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {

                Transferable contents = clipboard.getContents(CopyPasteFrame.this);
                text = (String) contents.getTransferData(DataFlavor.stringFlavor);

            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return text;
    }

    class ContentsMonitor implements Runnable {
        public void run() {
            String previous = getClipboardContents();

            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(null, "intropit ex");
                }
                String text = getClipboardContents();
                if (text != null && !text.equals(previous) && !text.equals(flag)) {
                    d.add(0, text);
                    previous = text;
                }
            }
        }
    }

}