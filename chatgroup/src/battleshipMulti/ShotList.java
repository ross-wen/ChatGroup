package battleshipMulti;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

public class ShotList {
	private ShotRecord[] list;
	private int maxSize;
	private int size;
	private SimpleDateFormat format;

	public ShotList() {
		this.maxSize = 200;
		list = new ShotRecord[maxSize];
		size = 0;
		format = new SimpleDateFormat("HH:mm:ss");
	}
	
	public boolean insert(ShotRecord record) {

		//if size is below maxSize
		if (size < maxSize) {
			size++;  //increase the current size
			list[size-1] = record;  //add record to the location just before
			return true;
		}
		return false;
	}
	
	public void bubbleSort() throws ParseException {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size - 1; j++) {
				Date date1 = new SimpleDateFormat("HH:mm:ss").parse(list[j].getsDate()); //date1 variable equals the records string that is converted to date. Parse is used to analyze the string and convert it to date.
				Date date2 = new SimpleDateFormat("HH:mm:ss").parse(list[j+1].getsDate()); //date 2 variable equals the next record string that is converted to date.
				if (date2.before(date1)) { //if date2 is before date 1
					ShotRecord temp;
					temp = list[j]; //move the 1st record into temp
					list[j] = list[j + 1];  //make the record switch with the next record
					list[j+1] = temp; //move the 2nd record into temp
				}
			}
		}
	}
	
	public void insertionSortUp() throws ParseException{
		for (int i = 1; i < size; i++) {
			ShotRecord temp = list[i]; //sets a temp record
			int k = i; //delcares and intiazlies k into i

			//Date date1 = new SimpleDateFormat("HH:mm:ss").parse(temp.getsDate());
			//Date date2 = new SimpleDateFormat("HH:mm:ss").parse(list[k-1].getsDate());

			while (k > 0 &&  format.parse(temp.getsDate()).before(format.parse(list[k-1].getsDate()))) { //if k is more than 0 and the temp record has a bigger trans amount then the last trans amount
				list[k] = list[k-1]; //swap records
				k--;
			}

			list[k] = temp; //put record into temp

		}
	}
	
	public void insertionSortDown() throws ParseException{
		for (int i = 1; i < size; i++) {
			ShotRecord temp = list[i]; //sets a temp record
			int k = i; //delcares and intiazlies k into i

			//Date date1 = new SimpleDateFormat("HH:mm:ss").parse(temp.getsDate());
			//Date date2 = new SimpleDateFormat("HH:mm:ss").parse(list[k-1].getsDate());

			while (k > 0 &&  format.parse(list[k-1].getsDate()).before(format.parse(temp.getsDate()))) { //if k is more than 0 and the temp record has a bigger trans amount then the last trans amount
				list[k] = list[k-1]; //swap records
				k--;
			}

			list[k] = temp; //put record into temp

		}
	}
	
	public int binarySearch (String searchKey) throws ParseException {
		int low = 0; //the first value of the array
		int high = size -1; //the highest value of the array
		int middle;



		while (low <= high) { //while the low variable is less than the high variable
			middle = (high + low) / 2; //the middle of the low and high variable
			Date date1 = new SimpleDateFormat("HH:mm:ss").parse(searchKey); //Parse the searchKey to find date1
			Date date2 = new SimpleDateFormat("HH:mm:ss").parse(list[middle].getsDate()); //Parse the middle record to find date2

			if (date1.equals(date2)) { //if dates are equal
				return middle;
			}

			else if (date1.before(date2) == true) { //if date1 comes before date2
				high = middle -1;
			}

			else {
				low = middle + 1;
			}

		}
		return -1;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public ShotRecord getRecord(int i) {
		return list[i];
	}
	
	public String toString() {
		//finish to String
		String out = "";
		for (int i = 0; i < size; i++) {
			out += list[i].toString() + "\n";
		}
		return out;
	}
	
	public static void main(String[] args) throws ParseException {
		ShotList list = new ShotList(); //creates new transaction list

		//inifnite loop
		while (true) {
			//enter input
			String input = JOptionPane.showInputDialog (null, "i - insert \n q - quit \n s - sort \n se - search \n bs - bubble sort");

			switch (input) {

			case "i": {
				//prompt for record
				String record = JOptionPane.showInputDialog(null, "Enter dd-MM-yyyy HH:mm/accountType/transType/transAmount/balance/endBalance", "10:20:45/10/5/Hit");
				//create record object
				ShotRecord tInfo = new ShotRecord();
				tInfo.processRecord(record);
				if (!list.insert(tInfo)) {
					JOptionPane.showMessageDialog(null, "adding failed");
				}
				break;
			}

			case "bs": {
				//bubble Sort
				list.bubbleSort();
				break;
			}

			case "se": {
				//bubble sort
				list.bubbleSort();

				//prompt for record to search
				String name = JOptionPane.showInputDialog(null, "Enter HH:mm:ss");

				//if the int is less than 0
				if (list.binarySearch(name) < 0) {
					JOptionPane.showMessageDialog(null, "Not Found");
				}

				else {
					JOptionPane.showMessageDialog(null, "Found");
				}
				break;
			}

			case "p": {
				//print the list
				System.out.println(list.toString());
			}


			}
		}
	}

}
