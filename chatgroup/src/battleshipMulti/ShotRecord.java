package battleshipMulti;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ShotRecord {
	
	private int row;
    private int col;
    private boolean hitOrMiss;
    private String userID;

    private Date date;
    private String sDate;
    private SimpleDateFormat format;
    
    public ShotRecord() {

        row = 0;
        col = 0;
        hitOrMiss = false;
        userID = "Player 1";


        format = new SimpleDateFormat("HH:mm:ss");
        date = new Date();
        sDate = format.format(date);
    }
    
    public ShotRecord(int r, int c, boolean hOrm, String id) {
        row = r;
        col = c;
        hitOrMiss = hOrm;
        userID = id;

        format = new SimpleDateFormat("HH:mm:ss");
        date = new Date();
        sDate = format.format(date);
    }
    
    public String getsDate() {
        return sDate;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean getHitOrMiss() {
        return hitOrMiss;
    }

    public void setRow(int r) {
        this.row = r;
    }

    public void setCol(int c) {
        this.col = c;
    }

    public void setHitOrMiss(boolean hitOrMiss) {
        this.hitOrMiss = hitOrMiss;
    }
    
    public void processRecord (String record) {
        //splits the record and stores it into an array of strings
        String[] words;
        words = record.split("/");

        //the date is set to the first of the array. The account type to the second word of the array and so on.
        this.sDate = words[0];
        this.row = Integer.parseInt(words[1]);
        this.col = Integer.parseInt(words[2]);
        this.hitOrMiss = Boolean.parseBoolean(words[3]);
    }
    
    public String toString() {
    	String loc = "";
    	loc = Character.toString((char)(row + 65));
    	loc += (col +1);
    	
    	String hOm = "";
    	if (hitOrMiss) {
    		hOm = "Hit";
    	}
    	else {
    		hOm = "Miss";
    	}
    	
    	
        return "[" + sDate + "] " + userID + " fired on " + loc + ", " + hOm;
    }


}
