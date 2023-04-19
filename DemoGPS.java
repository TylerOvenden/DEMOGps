package testok;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.*;

public final class DemoGPS extends Thread {
	//position class 
	public final class Position {
		//gga attributes
		public float utc;
		public float lat;
		public float lon;
		public int quality;
		public int satilites;		
		public float dilution;
		public float alt;
		public float seper;
		public int lastUp;
		public int checksum;
		
		
		//gsv attributes
		//init them to negative numbers as negative values for things like total messages isn't logical
	    //done so they're not init to 0 by default which could be an intended value 
		public int totMess = -999;
		public int messNum = -999;
		public int svView = -999;
		//same attributes for 4 different satelites 
		public int sv1PRN = -999;
		public int sv1Ele = -999;
		public int sv1Azimuth = -999;
		public int sv1SNR = -999;
		
		public int sv2PRN = -999;
		public int sv2Ele = -999;
		public int sv2Azimuth = -999;
		public int sv2SNR = -999;
		
		public int sv3PRN = -999;
		public int sv3Ele = -999;
		public int sv3Azimuth = -999;
		public int sv3SNR = -999;
		
		public int sv4PRN = -999;
		public int sv4Ele = -999;
		public int sv4Azimuth = -999;
		public int sv4SNR = -999;
		
		
		
	
	}
	
	//arraylist used to track all position objects created
	ArrayList<Position> arrPos = new ArrayList<Position>();
//	using atomicinteger to make it threadsafe
	AtomicInteger lastN;
	//counter for gga values created & added
	int ggaNum = 0;
	//use to access lastN, the number set for how many last positions are going to be used
	AtomicInteger getLast() {
		
		return lastN;
	}
	 
	public static void test() {
		//will run infinitely 
		while(true) { 
		try {
		//create a thread
			Thread t;
			t = new Thread();
			t.start();
			//load text file 
			
			InputStream input = new FileInputStream("coor.txt");
		
			//thread that will call the constructor 
			DemoGPS myThread = new DemoGPS(input, 3);
		   //will start the thread, reading the gps coordinates 
			myThread.start();
			
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 System.out.println("test");	
			}
		} 
		
	}
	
	//n = number of 
	DemoGPS(InputStream input, int n) {
		//bool used to decide if a new position object needs to be created or not
		boolean newPos = true;

		
	//convert n into an atomicinteger 
		AtomicInteger temp = new AtomicInteger(n);
		this.lastN = temp;
		
		
		//reads the inputstream line by line
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
	        while (reader.ready()) {
	        	try {
	        		
	        	String line = reader.readLine();
	            String[] split = line.split(",");
	            //create a new position object
	            Position  pos  = new Position();
	            //check if previously added position has gps coordinates or not
	          
	          
	            //check if reading a GGA command
	            //newPos = true;
	            //example of the GGA format
	            //$GPGGA,202530.00,5109.22,N,11441.82,W,5,40,0.5,1097.36,M,-17.00,M,18,TSTR*61
	            if(split[0].equals("$GPGGA")) {
	            	//  pos  = new Position();
	            
	            	//check if a gsv command was the first line, so you wouldn't need to add another position, just add gsv attributes to position 
	  	            if(arrPos.size()>0 && ggaNum == 0)
	  	            {	
	  	            	pos = arrPos.get(arrPos.size()-1);
	  	            	newPos = false;
	  	            }	
	      
		       //sets all the attributes based on each part of the parsed commanded
		            pos.utc = Float.parseFloat(split[1]);
		          
		            pos.lat =  Float.parseFloat(split[2]);
		            //if south, latitude is negative 
		            if(split[3].equals("S")) 
		            	pos.lat *= -1;
		            
		           
		            pos.lon = Float.parseFloat(split[4]);
		            //if west, longitude is negative 
		            if(split[5].equals("W")) 
			            pos.lon *= -1;
			        
		            pos.quality =  Integer.parseInt(split[6]); 
		            pos.satilites = Integer.parseInt(split[7]);
		            
		            
		            pos.dilution = Float.parseFloat(split[8]);
		            
		            pos.alt = Float.parseFloat(split[9]);
		            
		            pos.seper = Float.parseFloat(split[11]);
		            
		            pos.lastUp = Integer.parseInt(split[13]);
		            
		            
		            //split the last section to get checksum value
		            String[] split2 = split[14].split("\\*");
		            
		            pos.checksum = Integer.parseInt(split2[1]);
		         	            
		            //add to end of arraylist only if a new position object was created
		            if(newPos) {
		            	
		            	arrPos.add(pos);
		            }
		            //increment number of gga values 
		            ggaNum++;
		        
		            //reset boolean for next line
		            newPos = true;
		          //  System.out.println("size " +   arrPos.size());
	            }
	         
	            //checks if GSV command 
	            //GSV format: $GPGSV,3,2,11,14,25,170,00,16,57,208,39,18,67,296,40,19,40,246,00*74

	            if(split[0].equals("$GPGSV")) {
	            
            		newPos = true;
   
            		Position  p = new Position();
	            	//adds GSV data to the last added position object if there's one already added
	            	//need to create a new position if empty 	
	            	if(arrPos.size()>0 && arrPos.get(arrPos.size()-1).totMess == -999) {
	            		newPos = false;
	            		int last = arrPos.size()-1;
	            		//last position added 
	            		
	            	  p = arrPos.get(last);
	            	}
	            
	            	
	            	
	            		//set all the attributes
	            		p.totMess =  Integer.parseInt(split[1]); 
	            	
	            		p.messNum =  Integer.parseInt(split[2]); 
	            		p.svView =  Integer.parseInt(split[3]); 
	            		
	            		p.sv1PRN =  Integer.parseInt(split[4]); 
	            		p.sv1Ele =  Integer.parseInt(split[5]); 
	            		p.sv1Azimuth =  Integer.parseInt(split[6]); 
	            		p.sv1SNR =  Integer.parseInt(split[7]); 
	            		
	            		p.sv2PRN =  Integer.parseInt(split[8]); 
	            		p.sv2Ele =  Integer.parseInt(split[9]); 
	            		p.sv2Azimuth =  Integer.parseInt(split[10]); 
	            		p.sv2SNR =  Integer.parseInt(split[11]); 
	            		
	            		
	            		p.sv3PRN =  Integer.parseInt(split[12]); 
	            		p.sv3Ele =  Integer.parseInt(split[13]); 
	            		p.sv3Azimuth =  Integer.parseInt(split[14]); 
	            		p.sv3SNR =  Integer.parseInt(split[15]); 
	            		
	            		
	            		p.sv4PRN =  Integer.parseInt(split[16]); 
	            		p.sv4Ele =  Integer.parseInt(split[17]); 
	            		p.sv4Azimuth =  Integer.parseInt(split[18]); 
	            		
	            		//need to split last section again after the *
	            		 String[] split2 = split[19].split("\\*");
	 		            
	            		p.sv4SNR  = Integer.parseInt(split2[1]);
	            		
	            		//if a new position object was created, add to end of arraylist, else do nothing
	            		if(newPos) {
	            			 arrPos.add(pos);
	            			
	            	
	            		}
	            			//reset marker for new position 
	            		else
	            			newPos = true;
	            	
	            	}
	           
	        	}
	        	//would occur if unexpected field type, ex: float instead of int or string
	        	catch(NumberFormatException e){
		        	
		        	 System.out.println("incorrect data type");	
		        	
			  } 
	        	//array out of bounds would happen if there's not enough parts to the command
	        	catch(ArrayIndexOutOfBoundsException exception){
		        	
		        	 System.out.println("incorrect number of fields found in command ");	
		        	
			  }
	           // System.out.println(line);
	        }
	        getCurrentPos();
	    }catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	    	
	    	 System.out.println("file not found");	
	    	 e.printStackTrace();
	    } 
		   
		catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	
		  }
	
	//using synchronized to make the method thread safe
	//getCurrentPos() gets the average coordinates from the n last positions added
	 synchronized Position getCurrentPos(){
			
		//if array is empty just return null
		if(arrPos == null) {
			return null;
		}
		int n;
		
		//create a position object 
		 Position pos = new Position();
		 //check if the n amount of last position is greater than total number of positions tracked so far
	
		 int start = arrPos.size()-getLast().intValue();
		if(start < 0) {
		
			start = 0;
			n = arrPos.size();
		}
		else
			n = getLast().intValue();;
		
		float avgLong = 0;
		float avgLat = 0;
		//creating the total long & lat values by iterating through 
		System.out.println("longitude & lat values: ");
		for(int i = start; i < arrPos.size(); i++) {
			
			avgLat += arrPos.get(i).lat;
			avgLong += arrPos.get(i).lon;
			//prints to check the correct & right number of values for long & lat are being read
			System.out.println("i: " + i + " lat: " + arrPos.get(i).lat);
		    System.out.println("i: " + i + " long: " + arrPos.get(i).lon);
		}
		//averaging them to get a mean 
		avgLat /= n;
		avgLong /= n;
		
		//prints out the average longitude & latitude which will be used for the current position object
		System.out.println(" average lat " + avgLat);
		System.out.println(" average long " +avgLong);
		//sets the average longitude & latitude values
		pos.lat = avgLat;
		pos.lon = avgLong;
		//return the position 
			return pos;
		}
	
	 
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		test();	
	}

}
