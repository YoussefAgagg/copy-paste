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


public class CopyPasteFrame extends JPanel implements Serializable {

    private static final long serialVersionUID = 999999999999999999l;

    private static final String EXTENSION = ".txt";
//	private static final  LayoutFileFilter SAVE_AS_JO = 
//			  new LayoutFileFilter("text files Format(*.txt)", EXTENSION, true);


    DefaultListModel<String> d;
    private JList<String> textl;
    private JButton copy, remove, load, removeall;
    private String flag;
    private JButton saveAs;

    //private JSystemFileChooser write;
    //  private JFileChooser write,read;
    public CopyPasteFrame() {
        d = new DefaultListModel<String>();

        textl = new JList<String>(d);
        JPanel p = new JPanel(new FlowLayout());
        setLayout(new BorderLayout());
        textl.setAutoscrolls(true);
        textl.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        copy = new JButton("copy");
        copy.setBackground(Color.WHITE);
        remove = new JButton("remove");
        remove.setBackground(Color.WHITE);
        removeall = new JButton("removeAll");
        removeall.setBackground(Color.WHITE);
        //save=new JButton("save");
        load = new JButton("load");
        load.setBackground(Color.WHITE);
        load.setToolTipText("<html>load last copies you take from last time you opened the program<br>notice the list must be empty</html>");
        p.add(copy);
        p.add(remove);
        p.add(removeall);
        copy.setFocusable(false);
        remove.setFocusable(false);
        remove.setToolTipText("remove selection copy from the list");
        removeall.setFocusable(false);
        removeall.setToolTipText("remove all copies");
        load.setFocusable(false);
        textl.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                textl.setSelectedIndex(textl.getSelectedIndex());

            }
        });
        //  p.add(save);
        p.add(load);
        saveAs = new JButton("save as txt");
        saveAs.setFocusable(false);
        saveAs.setBackground(Color.WHITE);
        saveAs.setToolTipText("save all copies you took in text file");
        p.add(saveAs);
        saveAs.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
//					File f=chooseFile();
//					if(f!=null)
//					writeTXT(f);
//					
            }
        });
        //  setFocusable(true);
        // setFocusTraversalKeysEnabled(false);
        removeall.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                d.removeAllElements();

            }
        });

        //     MyCellRenderer cellRenderer = new MyCellRenderer(380);
        // textl.setCellRenderer(cellRenderer);
        textl.setFixedCellHeight(50);
        // textl.setCellRenderer(cellRenderer);
        textl.setFixedCellWidth(380);
        textl.setCellRenderer(getRenderer());
        p.setBackground(Color.BLACK);
        JScrollPane sPane = new JScrollPane(textl, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(p, BorderLayout.NORTH);
        textl.setBackground(Color.BLACK);
        textl.setForeground(Color.WHITE);
        sPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(50, 80, 100)));
        add(sPane);


        Thread t = new Thread(new ContentsMonitor());
        t.setDaemon(true);
        t.start();
        textl.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {


                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C && textl.getSelectedIndex() != -1)
                    flag = d.elementAt(textl.getSelectedIndex());

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

                        if (textl.getSelectedIndex() != -1) {
                            setSysClipboardText(d.get(textl.getSelectedIndex()));
                            flag = d.elementAt(textl.getSelectedIndex());
                        }

                    }
                }
        );
		  
		      /*
		      save.addActionListener(
						new ActionListener() 
						{
							public void actionPerformed(ActionEvent eveent) 
							{
								File file=writeFile();
								if(file!=null) {
									try {
									writePaint(file.getPath());
									} catch (IOException e) {
										e.printStackTrace();
									}
									}
								
								
							}
						}
						);*/
        load.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        File file = readFile();
                        if (file != null) {
                            try {
                                readPaint(file.getPath());
                                //file.delete();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                }
        );
    }

    public int get() {
        return d.getSize();
    }

    public File writeFile() {
        //write=new JFileChooser();
        //write.setPreferredSize(new Dimension(500, 400));
        //write.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        //int result=write.showSaveDialog(null);
        //if(result==JFileChooser.CANCEL_OPTION)
        //	return null;
        File name = new File("C:\\Users\\dell\\Documents\\hah.hah");
        return name;
    }

    public File readFile() {
        //read=new JFileChooser();
        //read.setPreferredSize(new Dimension(500, 400));
        //read.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        //int result=read.showOpenDialog(null);
        //if(result==JFileChooser.CANCEL_OPTION)
        //return null;
        File name = new File("C:\\Users\\dell\\Documents\\hah.hah");
        return name;
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
            e.printStackTrace();
            return null;
        }
        try {
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {

                Transferable contents = clipboard.getContents(CopyPasteFrame.this);
                text = (String) contents.getTransferData(DataFlavor.stringFlavor);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return text;
    }
/*
		    protected void setText(final String text) {
		      SwingUtilities.invokeLater(new Runnable() {
		        public void run() {
		        	//System.out.println(0);
		        //	if(d.getSize()!=0&&!d.getElementAt(d.getSize()-1).equals(text))
		        //  d.add(d.getSize(), text);
		        	
		        	
		        }
		      });
		    }*/

    class ContentsMonitor implements Runnable {
        public void run() {
            String previous = getClipboardContents();

            while (true) {
                try {
                    if (d.getSize() != 0)
                        load.setEnabled(false);
                    else load.setEnabled(true);
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(null, "intropit ex");
                }
                String text = getClipboardContents();
                if (text != null && !text.equals(previous) && !text.equals(flag)) {
                    //   setText(text);
                    //if(d.getSize()!=0)
                    d.add(0, text);
                    previous = text;

                }
            }
        }
    }

    /*
    class MyCellRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 1L;
        public static final String HTML_1 = "<html><body style='width: ";
           public static final String HTML_2 = "px'>";
           public static final String HTML_3 = "</html>";
           private int width;

           public MyCellRenderer(int width) {
              this.width = width;
           }

           @Override
           public Component getListCellRendererComponent(JList<?> list, Object value,
                 int index, boolean isSelected, boolean cellHasFocus) {
              String text = HTML_1 + String.valueOf(width) + HTML_2 + value.toString()
                    + HTML_3;
              return super.getListCellRendererComponent(list, text, index, isSelected,
                    cellHasFocus);
           }


}*/
    public void writePaint(String s) throws FileNotFoundException, IOException {
        try {
            //File name=s;
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(s));

            out.useProtocolVersion(ObjectOutputStream.PROTOCOL_VERSION_2);

            out.writeObject(d);


            out.flush();
            out.close();
        } catch (IOException ex) {
            System.out.println(ex + "ha1");
        }
    }

    public void writeTXT(File file) {
        try {
            //File name=s;
            file.setReadable(true);
            FileWriter fw = new FileWriter(file);

            // out.useProtocolVersion(ObjectOutputStream.PROTOCOL_VERSION_2);
            for (int i = 0; i < d.getSize(); i++) {
                fw.write(d.get(i));
                fw.write("\r\n");

            }
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }

    }

    @SuppressWarnings("unchecked")
    public void readPaint(String s) throws ClassNotFoundException {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(s));


            d = (DefaultListModel<String>) in.readObject();
            System.out.println(d.size());

            if (d.getSize() == 0) {
                JOptionPane.showMessageDialog(this, "Thee is no copies avalible");
            }

            textl.setModel(d);


            in.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Thee is no copies avalible");
            System.out.println(ex + "ha2");
        }
    }
//		    public File chooseFile() {
//				File selectedFile = null;
//				write=new JSystemFileChooser();
//				write.setFileSelectionMode(JFileChooser.FILES_ONLY);
//				ExtensionFileFilter pFilter = new ExtensionFileFilter(SAVE_AS_JO);
//			    write.setFileFilter(pFilter);
//			    
//				int result=write.showSaveDialog(this);
//				if(result==JFileChooser.CANCEL_OPTION)
//					return null;
//				if (result == JFileChooser.APPROVE_OPTION) {
//					selectedFile= write.getSelectedFile();
//
//			        try {
//			            String fileName = selectedFile.getCanonicalPath();
//			            if (!fileName.endsWith(EXTENSION)) {
//			                selectedFile = new File(fileName + EXTENSION);
//			            }
//			            ;
//			        } catch (IOException e) {
//			            e.printStackTrace();
//			        }
//			    }
//				//File name=write.getSelectedFile();
//				return selectedFile;
//			}
}