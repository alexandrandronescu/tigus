package org.tigus.app.editor;

import org.tigus.core.*;
import java.io.*;
import javax.swing.*;

import java.awt.event.*; 
import java.awt.*;

/**
 * Class for the main window of Question Editor GUI application
 * 
 * @author Adriana Draghici
 * 
 */
public class MainWindow extends JFrame implements ActionListener {
    
    /*
     * GUI components
     */
    JMenuBar menuBar;
    JToolBar toolBar;
    JMenuItem []menuItems;
    JMenu fileMenu;
    JMenu questionMenu;
    JButton []toolBarButtons;
    JTabbedPane tabbedPane;
    
    Boolean empty ; // true if the no question set is loaded in the window
    String qsPath;  // the last used path for loading/saving question sets
    QuestionSet qs; // the question set loaded/created in this window
    QuestionSetTab qsTab; //the tab showing the question set 
   
    
    /**
     * Class Constructor
     * @param none
     * @see JFrame
     */
    
    public MainWindow() {
        super("Question Editor");
        SwingUtilities.updateComponentTreeUI(this);
        setLocation(50,50);
        setPreferredSize(new Dimension(700,550));
        
        // add components : menu, toolbar, tooltips, panel
        initComponents();
        empty = true;
        qsPath = "";
      
        setTitle("Question Editor");
        setVisible(true);
        setDefaultLookAndFeelDecorated (true);
        pack();
        
        // Add a window listener for close button
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                showQuitDialog();
            }
        });        
    }
    
    /**
     * Initializes the frame's components: menus, toolbars and the tabbedpane as main panel
     * @param none
     * @return none
     */
    
    private void initComponents() {
        int i;
        
        String []menuItemsNames = {"New", "Open", "Save", "Save As",
                                    "Import", "Include", "Quit", 
                                    "Create", "Delete", "Review", "Move"};
        String []iconNames = {"images/newQS.png", "images/open.png", "images/save.png",
                                "images/saveas.png", "", "", "images/exit.png",
                                "images/create.png", "images/delete.png", "images/edit.png",
                                "images/switch.png"};                               
        
        // create menus
        
        menuItems = new JMenuItem[12];
        // menuItems 0->6  for fileMenu, menuItems 7->10 for questionMenu
        
        for (i = 0; i < 11; i++) {
            // initialize the JMenuItems components
            menuItems[i] = new JMenuItem(menuItemsNames[i], new ImageIcon(iconNames[i]));   
            
            // add listeners to the JMenuItems components
            menuItems[i].addActionListener(this);
        }
        
        // add tooltips to the JMenuItems components
        menuItems[0].setToolTipText("Create a new question set");
        menuItems[1].setToolTipText("Select a question set and load its content");
        menuItems[2].setToolTipText("Save the changes made to a question set");        
        
        menuItems[7].setToolTipText("Write a new question and save it to a question set");
        menuItems[8].setToolTipText("Delete a question");
        menuItems[9].setToolTipText("Review/edit a question and save the changes");
        
        menuItems[10].setToolTipText("Move questions between question sets");
        
        // set menu items' accelerators and mnemonics
        
        menuItems[0].setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuItems[1].setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuItems[2].setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuItems[3].setAccelerator( KeyStroke.getKeyStroke("shift ctrl S") );
        
        menuItems[0].setMnemonic('N');
        menuItems[1].setMnemonic('O');
        menuItems[2].setMnemonic('S');
        menuItems[3].setMnemonic('A');
        
        fileMenu = new JMenu("File");
        questionMenu = new JMenu("Question");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        for (i = 0; i < 6; i++) {
            fileMenu.add(menuItems[i]);
        }
        
        fileMenu.addSeparator();
        fileMenu.add(menuItems[6]);
        
        for (i = 7; i < 11; i++) {
            questionMenu.add(menuItems[i]);
            
            //disable question menu items 
            //These items are enabled only when a question set is loaded or created
            menuItems[i].setEnabled(false);
        }
        
        menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(questionMenu);
    
        setJMenuBar(menuBar);   
        
        // create toolbar
        
        toolBar = new JToolBar();
        toolBarButtons = new JButton[7];
        
        for (i = 0; i < 3; i++) {
            toolBarButtons[i] = new JButton(new ImageIcon(iconNames[i]));
            toolBarButtons[i].setActionCommand(menuItemsNames[i]);
            toolBarButtons[i].addActionListener(this);
            toolBar.add(toolBarButtons[i]);
        }
        
        toolBar.addSeparator();
        
        for (i = 3; i < 7; i++) {
            toolBarButtons[i] = new JButton(new ImageIcon(iconNames[i+4]));
            toolBarButtons[i].setActionCommand(menuItemsNames[i+4]);
            toolBarButtons[i].addActionListener(this);
            toolBar.add(toolBarButtons[i]);
        }
        
        add(toolBar, BorderLayout.PAGE_START);
        
        // add main componenet: JTabbedPane
        tabbedPane = new JTabbedPane();        
        
        tabbedPane.setPreferredSize(new Dimension(600,500));
        add(tabbedPane);
        
    }
    
    /**
     * Implementation of actionPerformed method inherited from ActionListener interface
     * @param ActionEvent 
     * @return none
     */
    
    public void actionPerformed(ActionEvent e) {
        
        String command  = e.getActionCommand();
        System.out.println(command);
        if (command.equals("Quit")) {
            showQuitDialog(); 
        }
        
        if (command.equals("New")) {
                qs = new QuestionSet();
                showQuestionSet(qs, "");            
        }
        
        if (command.equals("Open")) {
            qs = new QuestionSet();
            
            JFileChooser fileChooser = new JFileChooser();
            int action = fileChooser.showOpenDialog(this);
            
            if (action != JFileChooser.APPROVE_OPTION)
                return;
            File file = fileChooser.getSelectedFile();
            String qsPath = file.getPath();
            String qsName = file.getName();
            try { 
                qs.loadFromFile(qsPath);
               
            }catch(Exception ex){
                System.out.println(ex.toString());
            }
            
            showQuestionSet(qs, qsName);
            /*String qsName = (String)JOptionPane.showInputDialog(
                    this,
                    "Question Set's Name: "); 
            if (qsName != null) {
                try { 
                    qs.loadFromFile(qsName);
                   
                }catch(Exception ex){
                    System.out.println(ex.toString());
                }
                
                showQuestionSet(qs, qsName);
            }*/
        }
        if (command.equals("Save") || command.equals("Save As")) {
            JFileChooser fileChooser;
            
            if(qsPath.length() == 0) {
                fileChooser = new JFileChooser();
            }
            else  {
                fileChooser = new JFileChooser(qsPath);
            }
            
            int action = fileChooser.showSaveDialog(this);
            
            if (action != JFileChooser.APPROVE_OPTION)
                return;
            File file = fileChooser.getSelectedFile();
            qsPath = file.getPath();
            String qsName = file.getName();
            qsTab.showQuestionSetName(qsName);
            
            try {
                qs.saveToFile(qsPath);
            } catch(IOException ex) {
                System.out.println(ex.toString());
            }                
                        
        }
    }
    
    /**
     * Method used in case of closing the window or selecting Quit from File menu.
     * It prompts a confirm dialog
     * @param none
     * @return none
     */
    private void showQuitDialog()
    {
        int value = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to exit?",
                "exit", JOptionPane.YES_NO_OPTION);
       
        if (value == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
        
    }
    private void showQuestionSet(QuestionSet qs, String qsName)
    {
        
        if (empty == false){
            MainWindow newWindow = new MainWindow();
            newWindow.showQuestionSet(qs, qsName);
            return;
        }
        
        qsTab = new QuestionSetTab(tabbedPane, qs, qsName);
        qsTab.initComponents();
        menuItems[7].setEnabled(true);
        menuItems[8].setEnabled(true);
        menuItems[9].setEnabled(true);
        empty = false;
    }  
    
}


