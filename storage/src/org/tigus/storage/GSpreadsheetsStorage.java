
package org.tigus.storage;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.ListEntry;

import org.w3c.dom.Node;
import java.lang.Integer;
import java.util.*;
import java.net.URL;
import java.util.concurrent.*;

/**
 * Class for the Google Spreadsheets storage component
 * 
 * @author Alexandra Andronescu
 * 
 */

public class GSpreadsheetsStorage {

    private String username;
    private String password;
    
    private LinkedBlockingQueue<StudentGradesEntry> workpool;
    
    Worker worker;
    
    /**
     * @constructor
     */
    public GSpreadsheetsStorage(){
        workpool = new LinkedBlockingQueue<StudentGradesEntry>();
        worker = new Worker();
        worker.setWorkpool(this.workpool);
        worker.start();
    }
    
    public void end(){
        worker.setStop();
        try {
            worker.join();
        }
        catch(Exception e){}
    }
    
    /**
     * Function for retrieving the name of the plugin
     * 
     * @return name of the storage plug-in
     */
    public String getName(){
        return new String("Google GData Spreadsheets Library");
    }

    /**
     * Function for retrieving the version of the plugin
     * 
     * @return version of the plug-in
     * 
     *         format "x.y", where x and y are increasing integers
     */
    public String getVersion(){
        return new String("Google GData Spreadsheets Library Version 1.20.0");
    }

    /**
     * Function for retrieving the license of the plugin
     * 
     * @return array of Strings describing the license
     * 
     *         The license strings should include license information for the
     *         dependency classes.
     */
    public String[] getLicense(){
        String[] string = new String[6];
        string[0] = new String("Java JDK 1.5+ ");
        string[1] = new String("Apache Ant 1.7.1");
        string[2] = new String("JavaMail 1.4.1 ");
        string[3] = new String("Java Activation JAF 1.1.1 ");
        string[4] = new String("Apache Tomcat 5.5 ");
        string[5] = new String("GDdata 1.20.0");
        return string;
    }

    /**
     * Function for retrieving the XML namespace for of the configuration node
     * used for storing plugin-specific configuration options
     * 
     * @return String containing XML namespace for the configuration node
     */
    public String getXmlNS(){
        return "";
    }

    /**
     * Function for loading the configuration from an XML node
     * 
     * @param xmlConfigurationNode
     *        node in the configuration file holding plugin-specific settings
     * @return true if configuration was successful
     * 
     *         The XML configuration node is unique per plug-in instance. There
     *         is no such thing as global settings for all plug-in instances.
     * 
     *         The XML configuration node can be null. In this situation, a
     *         default configuration can be assumed.
     */
    public boolean loadConfiguration(Node xmlConfigurationNode){
        this.username = new String("storage.tigus.project");
        this.password = new String("tigusPROJECT2008");
        worker.setUsernamePassword(this.username, this.password);
        return true;
    }

    /**
     * Function for loading the configuration from an XML node
     * 
     * @param xmlConfigurationNode
     *        node in the configuration file holding plugin-specific settings
     * @return true if configuration was successful
     * 
     *         The XML configuration node is unique per plug-in instance. There
     *         is no such thing as global settings for all plug-in instances.
     *         The node for the saved configuration might be different from the
     *         node where the configuration was loaded from.
     */
    public boolean saveConfiguration(Node xmlConfigurationNode){
        this.username = new String("storage.tigus.project");
        this.password = new String("tigusPROJECT2008");
        worker.setUsernamePassword(this.username, this.password);
        return true;
    }

    /**
     * Function for writing an entry to the data store.
     * 
     * @param entry
     *        containing information about
     * 
     *        This function returns immediately. Writing to the data store is
     *        done asynchronously. The Storage implementation should maintain a
     *        queue of not yet written entries and try writing them from time to
     *        time.
     * 
     *        Status string should be reported through getStatusString()
     *        function, number of pending writes should be reported through
     *        getPendingWritesCount() function.
     */
    public void write(StudentGradesEntry entry){
        try{
            workpool.put(entry);
        }
        catch(Exception e){}
    }

    /**
     * Function for reading all entries from the data store
     * 
     * @return vector with all entries in the data storage
     */
    public List<StudentGradesEntry> readAll(){
        Vector<StudentGradesEntry> entryList = new Vector<StudentGradesEntry>();
        try{
            SpreadsheetService service = new SpreadsheetService("Tigus Project Storage");
            service.setUserCredentials(this.username, this.password);
            
            URL metafeedUrl = new URL("http://spreadsheets.google.com/feeds/spreadsheets/private/full");
            SpreadsheetFeed feed = service.getFeed(metafeedUrl, SpreadsheetFeed.class);
            List<SpreadsheetEntry> spreadsheets = feed.getEntries();
            for (int i = 0; i < spreadsheets.size(); i++) {
                SpreadsheetEntry entry = spreadsheets.get(i);
                //System.out.println("\t" + entry.getTitle().getPlainText());
                  
                List<WorksheetEntry> worksheets = entry.getWorksheets();
                for (int j = 0; j < worksheets.size(); j++) {
                    WorksheetEntry worksheet = worksheets.get(j);
                    //String title = worksheet.getTitle().getPlainText();
                    //int rowCount = worksheet.getRowCount();
                
                    URL listFeedUrl = worksheet.getListFeedUrl();
                    ListFeed f = service.getFeed(listFeedUrl, ListFeed.class);
                    
                    for (ListEntry e : f.getEntries()) {
                        //System.out.println(e.getTitle().getPlainText());
                        StudentGradesEntry sge = new StudentGradesEntry();
                    
                        Iterator<String> it = e.getCustomElements().getTags().iterator();

                        sge.id = e.getCustomElements().getValue(it.next());
                        sge.testSerialNumber = e.getCustomElements().getValue(it.next());
                        sge.studentName = e.getCustomElements().getValue(it.next());
                        sge.studentGroup = e.getCustomElements().getValue(it.next());
                        
                        sge.mapQuestionPosition = null;
                        sge.mapQuestionGrade = null;        
                        sge.mapQuestionAnswer = null;  
                        sge.total = null;
                        
                        entryList.add(sge);
                    }      
                    
                }
            }
        }
        catch(Exception e){
            System.out.println("Exception caught!");
        }
        return entryList;
    }

    /**
     * Function for reading an entry from the data store based on the entry ID
     * 
     * @param entryId
     *        identifies the entry in the data storage
     * @return entry that was read or null if entry does not exist
     */
    public StudentGradesEntry read(String entryId){
        try{
            SpreadsheetService service = new SpreadsheetService("Tigus Project Storage");
            service.setUserCredentials(this.username, this.password);
            
            URL metafeedUrl = new URL("http://spreadsheets.google.com/feeds/spreadsheets/private/full");
            SpreadsheetFeed feed = service.getFeed(metafeedUrl, SpreadsheetFeed.class);
            List<SpreadsheetEntry> spreadsheets = feed.getEntries();
            for (int i = 0; i < spreadsheets.size(); i++) {
                SpreadsheetEntry entry = spreadsheets.get(i);
                //System.out.println("\t" + entry.getTitle().getPlainText());
                  
                List<WorksheetEntry> worksheets = entry.getWorksheets();
                for (int j = 0; j < worksheets.size(); j++) {
                    WorksheetEntry worksheet = worksheets.get(j);
                    //String title = worksheet.getTitle().getPlainText();
                
                    URL listFeedUrl = worksheet.getListFeedUrl();
                    ListFeed f = service.getFeed(listFeedUrl, ListFeed.class);
                    
                    for (ListEntry e : f.getEntries()) {
                        //System.out.println(e.getTitle().getPlainText());
                        StudentGradesEntry sge = new StudentGradesEntry();

                        Iterator<String> it = e.getCustomElements().getTags().iterator();

                        sge.id = e.getCustomElements().getValue(it.next());
                        if( entryId.equals(sge.id)){
                            sge.testSerialNumber = e.getCustomElements().getValue(it.next());
                            sge.studentName = e.getCustomElements().getValue(it.next());
                            sge.studentGroup = e.getCustomElements().getValue(it.next());
                            
                            sge.mapQuestionPosition = null;
                            sge.mapQuestionGrade = null;        
                            sge.mapQuestionAnswer = null;  
                            sge.total = null;
                            
                            return sge;
                        }
                  
                    }      
                }
            }
        }
        catch(Exception e){
            System.out.println("Exception caught!");
        }
        return null;
    }

    /**
     * Function for deleting an entry from the data store based on the entry ID
     * 
     * @param entryId
     *        identifies the entry in the data storage
     */
    public void delete(String entryId){
    }

    /**
     * Function for determining the status for the actions requested from the
     * data store. Returns STATUS_* values
     * 
     * @return status integer (STATUS_*)
     */
    public int getStatus(){
        return 0;
    }

    /**
     * Function for getting the status string.
     * 
     * @return status string
     * 
     *         This function is being called when the status of the data store
     *         changes to something different than STATUS_COMPLETED. It should
     *         return a string describing the reasons for the new status.
     */
    public String getStatusString(){
        return null;
    }

    /**
     * Function for getting the number of pending writes
     * 
     * @return number of pending writes
     */
    public int getPendingWritesCount(){
        return 0;
    }

    /**
     * Function for drawing the plugin configuration on a panel
     * 
     * @param parentPanel
     *        panel used for drawing the configuration interface
     */
    public void drawConfigurationPanel(javax.swing.JPanel parentPanel){
    }

    /**
     * Function called when closing the configuration panel This function should
     * save the parameters from the panel into local plugin variables
     * 
     * @param parentPanel
     *        panel used for drawing the configuration interface
     */
    public void closeConfigurationPanel(javax.swing.JPanel parentPanel){
    }
   
    public static void main(String[] args) {
        GSpreadsheetsStorage gss = new GSpreadsheetsStorage();
        
        StudentGradesEntry sge = new StudentGradesEntry();
        sge.id = new String("5");
        sge.author = new String("Ionescu Ana");
        sge.testSerialNumber = new String("36");
        sge.studentName = new String("Ene Andreea");
        sge.studentGroup = new String("332CA");
        
        sge.mapQuestionPosition = new HashMap<Integer, Integer>();
        sge.mapQuestionPosition.put(423, 1);
        sge.mapQuestionPosition.put(865, 2);
        sge.mapQuestionPosition.put(754, 3);
        sge.mapQuestionPosition.put(144, 4);
        sge.mapQuestionPosition.put(234, 5);

        sge.mapQuestionGrade = new HashMap<Integer, Float>();
        sge.mapQuestionGrade.put(423, 10.0f);
        sge.mapQuestionGrade.put(865, 10.0f);
        sge.mapQuestionGrade.put(754, 10.0f);
        sge.mapQuestionGrade.put(144, 10.0f);
        sge.mapQuestionGrade.put(234, 10.0f);        
        
        sge.mapQuestionAnswer = new HashMap<Integer, Integer>();
        sge.mapQuestionAnswer.put(423, 4);
        sge.mapQuestionAnswer.put(865, 2);
        sge.mapQuestionAnswer.put(754, 1);
        sge.mapQuestionAnswer.put(144, 4);
        sge.mapQuestionAnswer.put(234, 3);  

        sge.total = 50.0f;
        
        gss.saveConfiguration(null);
        gss.write(sge);
        sge = gss.read("2");
        /*
        if(sge!=null)
            System.out.println(sge.testSerialNumber);
        else
            System.out.println("null");
        */
        Iterator<StudentGradesEntry> it = gss.readAll().iterator();
        while(it.hasNext()){
            sge = it.next();
            //System.out.println(sge.testSerialNumber);
        }
        gss.end();
    }    
}
