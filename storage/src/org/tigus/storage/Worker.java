
package org.tigus.storage;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

class Worker extends Thread {
    private String username, password;
    private LinkedBlockingQueue<StudentGradesEntry> workpool;
    int stop;
    
    public Worker(){ }
    
    public Worker(String username, String password){
        this.username = username;
        this.password = password;
        this.stop = 0;
    }
    
    public void setUsernamePassword(String username, String password){
        this.username = username;
        this.password = password;
    }
    
    public void setStop(){
        this.stop = 1;
    }      
    
    public void setWorkpool(LinkedBlockingQueue<StudentGradesEntry> lbq){
        this.workpool = lbq;
    }    
    
    public void run(){
        while ( this.stop==0 ) {
            //if( workpool.isEmpty()==false ){
            if( this.workpool.isEmpty()==false ){
                try {
                    StudentGradesEntry sge = (StudentGradesEntry)this.workpool.poll(5L, TimeUnit.SECONDS);
                    //StudentGradesEntry sge = (StudentGradesEntry)workpool.poll(5L, TimeUnit.SECONDS);
                    write(sge);
                }
                catch (InterruptedException e) { return ;}    
            }
        }
    }
    
    public void write(StudentGradesEntry entry){
        try{
            SpreadsheetService service = new SpreadsheetService("Tigus Project Storage");
            service.setUserCredentials(this.username, this.password);
            
            URL metafeedUrl = new URL("http://spreadsheets.google.com/feeds/spreadsheets/private/full");
            SpreadsheetFeed feed = service.getFeed(metafeedUrl, SpreadsheetFeed.class);
            List<SpreadsheetEntry> spreadsheets = feed.getEntries();
            for (int i = 0; i < spreadsheets.size(); i++) {
                SpreadsheetEntry en = spreadsheets.get(i);
              
                List<WorksheetEntry> worksheets = en.getWorksheets();
                for (int j = 0; j < worksheets.size(); j++) {
                    WorksheetEntry worksheet = worksheets.get(j);
                    //String title = worksheet.getTitle().getPlainText();
                    URL listFeedUrl = worksheet.getListFeedUrl();
                    service.getFeed(listFeedUrl, ListFeed.class);
                    ListEntry newEntry = new ListEntry();
                    /*
                    String nameValuePairs = new String("EntryID=4,TestID=456789,Grupa=334CA,Nume=Ene Andreea,PunctajTotal=45,Punctaj1=5,Punctaj2=10,Punctaj3=10,Punctaj4=10,Punctaj5=10");
                    for (String nameValuePair : nameValuePairs.split(",")) {
                      String[] parts = nameValuePair.split("=", 2);
                      String tag = parts[0]; 
                      String value = parts[1]; 
                      newEntry.getCustomElements().setValueLocal(tag, value);
                    }*/

                    newEntry.getCustomElements().setValueLocal("EntryID", entry.id);
                    newEntry.getCustomElements().setValueLocal("TestID", entry.testSerialNumber);
                    newEntry.getCustomElements().setValueLocal("Grupa", entry.studentGroup);
                    newEntry.getCustomElements().setValueLocal("Nume", entry.studentName);
                    newEntry.getCustomElements().setValueLocal("PunctajTotal", entry.total+"");
                    
                    Iterator<Integer> it = entry.mapQuestionPosition.keySet().iterator();
                    while(it.hasNext()){
                        Integer key = (Integer)it.next();
                        newEntry.getCustomElements().setValueLocal("Punctaj"+key, entry.mapQuestionGrade.get(key)+"");
                    }
                    try{
                        service.insert(listFeedUrl, newEntry);            
                    }
                    catch (ServiceException se){
                        se.printStackTrace();
                    }
                }
            }
        }      
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
