package org.tigus.app.editor;

import java.awt.Dimension;
import java.awt.event.*;
import java.awt.GridLayout;
import javax.swing.event.*;
import javax.swing.*;

import java.util.*;

import org.tigus.core.*;

/**
 *  This class represents a tab with components that permit editing a new Question
 * @author Adriana Draghici
 *
 */

class QuestionTabEdit {
        
    int listIndex;
    int tabIndex;
    int correctCount;
    Boolean isCorrect;
    String state;
   
    QuestionSet questionSet;
    Question question;
    TagSet tagSet;
    String qsName;

    Vector <Answer> answers;
    
    QuestionSetTab qsTab;
    /* Tab's components */
    
    JTabbedPane tabbedPane;
    JTextArea questionTextArea = new JTextArea();
    JTextArea answerTextArea = new JTextArea();   
    JTextField tagTextField = new JTextField();
    JTextField tagValueTextField = new JTextField();

       
    JButton newButton = new JButton("New answer");
    JButton deleteButton = new JButton("Delete answer");
    JButton saveButton = new JButton("Apply");
    JButton tagButton = new JButton("Apply");
    JButton removeTagButton = new JButton("Remove tag");
    
    JButton okButton = new JButton("Ok");       
    JButton cancelButton = new JButton("Cancel");
    
    JLabel tagValueLabel = new JLabel("");    
    JComboBox tagsComboBox = new JComboBox();    
    JCheckBox correctCheckBox = new JCheckBox("Correct");
    
    JScrollPane answerScrollPane = new JScrollPane(answerTextArea);
    JScrollPane questionScrollPane = new JScrollPane(questionTextArea);
    JScrollPane listScrollPane;
    
    JPanel mainPanel;
    Vector <String> tagsNames = new Vector<String>();
    DefaultListModel listModel = new DefaultListModel();
    DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
    JList answersList;
 
    /**
     * Class's constructor
     * @param pane - the JTabbedPane object in which to add the new tab
     * @param question - the question to be edited
     * @param qs - the QuestionSet object containing the question to be edited
     * @param qsName - the name of the question set
     */ 
 
    
    public QuestionTabEdit(QuestionSetTab qsTab, JTabbedPane pane, Question question , 
                            QuestionSet qs, String qsName) {
        this.qsTab = qsTab;
        this.tabbedPane = pane;   
        this.question = question;
        this.questionSet = qs;
        this.qsName = qsName;
        
        isCorrect = false; 
        state = "ADD"; 
        correctCount = 0;
        //initComponents();
    }
    
    /**
     * Builds a list of the question's answers    
     */
    private void showAnswers() {
        
        answers = new Vector<Answer>(question.getAnswers());
        
        // build a list model containing the question's answer 
        String s;
        for(int i = 0; i < answers.size(); i++){
            if (answers.elementAt(i).isCorrect() == true){
                s =  "<html><ul><li type=circle>";
                correctCount++;
            }
            else s = "<html><ul><li type=disc> ";
            listModel.addElement(s + answers.elementAt(i).getText()+"</ul></html>");
        }
        // add the list model to the list component
        answersList = new JList(listModel);
        listIndex = -1;
    }
    
    
    private void updateAnswersList(Boolean c, String text, int index) {
        
         if (state.equals("ADD")) { 
          // System.out.println("la adaugare in vectorul answers");
            answers.addElement(new Answer(c, text));  
            
            String s = new String();
            if (c){
                s =  "<html><ul><li type=circle>";    
            }
            else  {
                s = "<html><ul><li type=disc> ";
            }
          // System.out.println("la adaugare in list model");
            listModel.addElement(s + text +"</ul></html>");
         
            answersList.setSelectedIndex(answers.size()-1); 
            state = "EDIT";
          // System.out.println("la terminare adaugare raspuns nou");
            return;
        }
        
        if (state.equals("DEL")) {            
            answers.remove(index);                     
            listModel.remove(index);
            answersList.setSelectedIndex(0);
            state = "EDIT";
            return;
        }
        
        if (state.equals("EDIT")) {            
            answers.setElementAt(new Answer(c,text), index);
            
            String s = new String();
            if (c){
                s = "<html><ul><li type=circle> ";      
            }
            else  {
                s = "<html><ul><li type=disc> ";
            }
            
            listModel.setElementAt(s+text+"</ul></html>", index);
            answersList.setSelectedIndex(index);  
           
        }
    }
    /**
     * Adds tags' names to the class's JComboBox object   
     */
    private void showTags() {
      
        // get tags
        tagSet = question.getTags(); 
        if (tagSet.isEmpty()) {
            tagsComboBox.setModel(comboBoxModel);
            return;
        }
        Set <String> keys = tagSet.keySet();
        
        // insert tags' names into comboBox
       
        for (Iterator <String> it = keys.iterator(); it.hasNext(); ) {           
           String tagName = new String(it.next());
           comboBoxModel.addElement(tagName);  
         
           tagsNames.addElement(tagName);        
        }
        tagsComboBox.setModel(comboBoxModel);
        tagsComboBox.setEditable(false);
        tagsComboBox.setSelectedItem(0);
       
        showTagValues((String)tagsComboBox.getSelectedItem());
         
    }
    
    /**
     * Retrieves and display's the values of a given tag in class's JLabel object
     * @param tagName - the name of the tag
     */
    private void showTagValues(String tagName) {        
                
        // get tag's values
        Vector <String> values = new Vector<String>(tagSet.get(tagName));
        String text = new String();
        text += values.elementAt(0);
        
        for (int i = 1; i < values.size(); i++){                    
            text += ", ";
            text += values.elementAt(i);
        }
        
        tagValueLabel.setText(text);
    }
    /**
     * Initializes the main panel's components and containers 
     * by setting their size, their layout and their listeners.
     * @params none
     * @return none
     */
    
    public void initComponents() {
               
        showAnswers();
        showTags();
       
        questionTextArea.setText(question.getText());       
        
        /*
         * set components size
         */ 
        
        newButton.setPreferredSize(new Dimension(100,25));
        deleteButton.setPreferredSize(new Dimension(100,25));
        saveButton.setPreferredSize(new Dimension(100,25));
        tagButton.setPreferredSize(new Dimension(100,25));
        removeTagButton.setPreferredSize(new Dimension(200,25));
        okButton.setPreferredSize(new Dimension(100,25));
        cancelButton.setPreferredSize(new Dimension(100,25));
        questionTextArea.setPreferredSize(new Dimension(300,100));
        answerTextArea.setPreferredSize(new Dimension(300,100));
        
        
        answersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listScrollPane = new JScrollPane(answersList);
        
        
        /* set components layout */        
        
        mainPanel = setLayout(); 
        
        
        /* add new tab */
        
        tabbedPane.addTab("Question", mainPanel);
        tabIndex = tabbedPane.getTabCount() -1;
        tabbedPane.setSelectedIndex(tabIndex); //set focus
        
        /* add listeners */
        
        addListeners();
          
       
    }
    
    /**
     * Places the class's GUI components into panels
     * @params none
     * @return JPanel object
     */
    private JPanel setLayout() {
        
        JPanel questionPanel = new JPanel();
        JPanel answerPanel = new JPanel();
        JPanel tagsPanel = new JPanel();
        JPanel buttonsPanel = new JPanel();
        JPanel confirmPanel = new JPanel();
        
        final JPanel verticalPanel = new JPanel();      

        questionScrollPane.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        listScrollPane.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        answerScrollPane.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        correctCheckBox.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        saveButton.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        
        questionPanel.add(questionScrollPane);
        questionPanel.add(listScrollPane);
        questionPanel.setBorder(BorderFactory.createTitledBorder("Text"));
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
       
        buttonsPanel.add(newButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        
        answerPanel.add(buttonsPanel); 
        answerPanel.add(correctCheckBox);
        answerPanel.add(answerScrollPane);
        answerPanel.add(saveButton);
        answerPanel.setBorder(BorderFactory.createTitledBorder("Answer"));
        answerPanel.setLayout(new BoxLayout(answerPanel, BoxLayout.Y_AXIS));
        
        
        JPanel gPanel = new JPanel(); 
        gPanel.setLayout(new GridLayout(0,2));
        
        gPanel.add(new JLabel("Tags: "));
        gPanel.add(new JLabel("Values: "));        
        gPanel.add(tagsComboBox);
        gPanel.add(tagValueLabel);
        gPanel.add(tagTextField);
        gPanel.add(tagValueTextField);
        gPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        
        JPanel hPanel = new JPanel();
        hPanel.add(tagButton);
        hPanel.add(removeTagButton);
        hPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        hPanel.setLayout(new BoxLayout(hPanel, BoxLayout.X_AXIS));
        
        tagsPanel.add(gPanel);
        tagsPanel.add(hPanel);
        
        tagsPanel.setLayout(new BoxLayout(tagsPanel, BoxLayout.Y_AXIS));
        tagsPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        tagsPanel.setBorder(BorderFactory.createTitledBorder("tags"));
        
        
        confirmPanel.add(okButton);
        confirmPanel.add(cancelButton);     
        confirmPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        
        
        verticalPanel.add(questionPanel);       
        verticalPanel.add(answerPanel);
        verticalPanel.add(tagsPanel);
        verticalPanel.add(confirmPanel);
        verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
        
        return verticalPanel;
    }
    
    /**
     * Add listeners to the class's GUI components (buttons, checkbox)
     * @params none
     */
    
    private void addListeners() {
        
        answersList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (state.equals("DEL")) {
                    answerTextArea.setText("");
                    correctCheckBox.setSelected(false);
                    return;
                }
                state = "EDIT";
                
                listIndex = answersList.getSelectedIndex();
                System.out.println("index : " + listIndex);
                String answerText = answers.elementAt(listIndex).getText();
                
                // sets the component's content
                answerTextArea.setText(answerText);
               
                if (answers.elementAt(listIndex).isCorrect()) {
                    correctCheckBox.setSelected(true);
                    isCorrect = true;
                }
                else {                   
                    correctCheckBox.setSelected(false);
                    isCorrect = false;
                }
             
            }
        });
        
        tagsComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tagName = (String)tagsComboBox.getSelectedItem();
                showTagValues(tagName);                
            }
        });
                
        correctCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                   isCorrect = true;
                  
                  }
                else {                    
                    isCorrect = false;
                }
                
            }
            
        }) ;
        
        newButton.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                state = "ADD";
                answerTextArea.setText("");
                correctCheckBox.setSelected(false);     
                
            }
        });
        
        deleteButton.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
               state = "DEL";           
               if (isCorrect == true)
                   correctCount = 0;
               if (listIndex == -1) {
                   JOptionPane.showMessageDialog(mainPanel,
                           "Please select an answer!", 
                               "", JOptionPane.ERROR_MESSAGE);
                   return;
                }
               
               String answerText = answerTextArea.getText();
                      
                              
               question.getAnswers().remove(new Answer(
                                   answers.elementAt(listIndex).isCorrect(), answerText));
              
               updateAnswersList(null, null, listIndex);
             
               
            }
        });
        
        saveButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               
               String answerText = answerTextArea.getText();
               
               if (answerText.length() == 0) {
                   JOptionPane.showMessageDialog(mainPanel,
                           "The answer's text is empty!", 
                               "", JOptionPane.ERROR_MESSAGE);
                   return;
               }
               if (isCorrect) {
                   System.out.println("correct count before ++ = " + correctCount);
                   correctCount ++;
                   
               }
               if(state.equals("EDIT")) {
                   if(!isCorrect &&  answers.elementAt(listIndex).isCorrect()) {
                       // a correct answer was marked as not correct
                       correctCount = 0;
                   }
               }
               if (isCorrect && correctCount > 1){
                   JOptionPane.showMessageDialog(mainPanel,
                           "There must be only ONE correct answer!", 
                               "Error", JOptionPane.ERROR_MESSAGE);    
                   System.out.println("aici la isCorrect == true si correctCount > 1");
                   correctCount--;
                   return;
               }
               
               if(state == "ADD") {
                  // System.out.println("inainte de adaugare raspuns la intrebare");
                   question.addAnswer(isCorrect, answerText); 
                 //  System.out.println("dupa adaugare raspuns la intrebare");
                   updateAnswersList(isCorrect, answerText, 0);
               }
               if(state == "EDIT") {
                 //  System.out.println("la EDIT");
                   question.getAnswers().set(listIndex, new Answer(isCorrect, answerText));               
                   updateAnswersList(isCorrect, answerText, listIndex);
               }
               
               
               state = "EDIT";
           }
            
        });
        
        tagButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tagName = tagTextField.getText();
                String tagValues = tagValueTextField.getText();
                Boolean tagExists = tagsNames.contains(tagName);
                if (tagName.length() == 0) {
                    JOptionPane.showMessageDialog(mainPanel,
                         "Please insert the tag's name!", 
                             "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (tagValues.length() == 0) {
                    JOptionPane.showMessageDialog(mainPanel,
                            "Please insert the tag's values!", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                       return;
                }
                
                if(tagExists) {                    
                 
                    int value = JOptionPane.showConfirmDialog(mainPanel,
                            "Are you sure you want to change tag's values?",
                            "", JOptionPane.YES_NO_OPTION);
                   
                    if (value == JOptionPane.NO_OPTION) {
                        return;
                    }                     
                    
                    comboBoxModel.removeElement(tagName);
                    
                }
                comboBoxModel.addElement(tagName);
                question.setTagValueList(tagName, tagValues);                  
                
                // clear components
                tagTextField.setText("");
                tagValueTextField.setText("");
            }
            
        });
        
        removeTagButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tagName = (String)tagsComboBox.getSelectedItem();
                
                int value = JOptionPane.showConfirmDialog(mainPanel,
                        "Are you sure you want to remove " + tagName +"tag?",
                        "", JOptionPane.YES_NO_OPTION);
               
                if (value == JOptionPane.NO_OPTION) {
                    return;
                } 
                tagSet.remove(tagName);
                comboBoxModel.removeElement(tagName);
            }
        });
        
        okButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               String questionText = questionTextArea.getText();
               if (questionText.length() == 0) {
                   JOptionPane.showMessageDialog(mainPanel,
                           "The question's text is empty!", 
                               "", JOptionPane.ERROR_MESSAGE);
                   return;
               }
               
               if(correctCount == 0) {
                   JOptionPane.showMessageDialog(mainPanel,
                           "There is no correct answer!", 
                               "warning", JOptionPane.WARNING_MESSAGE);
               }
               
               question.setId(questionText);
              
               try {
                   questionSet.saveToFile(qsName);
               }catch(Exception ex) {}
               
               qsTab.updateQuestionsList("EDIT", question);
               tabbedPane.removeTabAt(tabIndex);
               
               
               
           }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int value = JOptionPane.showConfirmDialog(mainPanel,
                        "Are you sure you want to cancel?",
                        "", JOptionPane.YES_NO_OPTION);
               
                if (value == JOptionPane.YES_OPTION) {
                    tabbedPane.removeTabAt(tabIndex);
                }                   
            }
        });
    }

}

    