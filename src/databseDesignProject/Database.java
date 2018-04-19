package databseDesignProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;



public class Database {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/?user=root&password=root");
		
		Statement st = con.createStatement();
		
		st.executeUpdate("CREATE database if not exists Bookings");
		
		createTables();
		tablePopulation();
		
		Booking.launchGUI(args);

	}


	private static void createTables() throws SQLException {

		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bookings?user=root&password=root");

		Statement st = con.createStatement();
		
		Booking.setSt(st);
	
		
		st.executeUpdate("CREATE TABLE if not exists Rooms ("
				+ "roomNo INT,"
				+ "maximumOccupancy INT NOT NULL,"
				+ "noOfSingles INT,"
				+ "noOfDoubles INT,"
				+ "price DOUBLE NOT NULL,"
				+ "mountainView BOOLEAN,"
				+ "oceanView BOOLEAN,"
				+ "balcony BOOLEAN,"
				+ "PRIMARY KEY(roomNo))");
		
		st.executeUpdate("CREATE TABLE if not exists CustomerDetails ("
				+ "customerID INT AUTO_INCREMENT,"
				+ "firstName VARCHAR(20),"
				+ "surName VARCHAR(20),"
				+ "telNo INT NOT NULL UNIQUE,"
				+ "PRIMARY KEY(customerID))");
		
		st.executeUpdate("CREATE TABLE if not exists Reservation ("
						+ "roomNo INT,"
						+ "checkInDate DATE NOT NULL,"
						+ "checkOutDate DATE NOT NULL,"
						+ "noOfGuests INT NOT NULL,"
						+ "customerID INT NOT NULL,"
						+ "PRIMARY KEY(customerID),"
						+ "FOREIGN KEY(roomNo) references Rooms(roomNo),"
						+ "FOREIGN KEY(customerID) references CustomerDetails(customerID))");
		
		st.executeUpdate("CREATE TABLE if not exists paymentDetails ("
						+ "customerID INT NOT NULL, "
						+ "cardNumber VARCHAR(10) NOT NULL,"
						+ "paymentMethod SET('Visa', 'Debit', 'Credit'),"
						+ "amount INT NOT NULL,"
						+ "PRIMARY KEY(customerID),"
						+ "FOREIGN KEY(customerID) references CustomerDetails(customerID))");

		
	}
	
	private static void tablePopulation() throws SQLException {

		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bookings?user=root&password=root");

		Statement st = con.createStatement();
		
		int numberOfRooms = 500;
		int numberOfFloors = 10;
		int roomNumber = 100;
		int max = 6;
		int min = 1;;
		
		for(int count = 0; count < numberOfFloors; count++)
		{
			roomNumber = (count+1) * 100;
			for(int count2 = 0; count2 < (numberOfRooms/numberOfFloors); count2++)
			{
				roomNumber++;
				int occupants = randomGenerate(max, min);
				int temp = randomGenerate((occupants/2 > 2) ? 2 : occupants/2 , 0);
				int doubles = (occupants >= 2) ? (temp == 0 && occupants >= 5) ? 1 : temp : randomGenerate(1, 0);
				
				int singles = (occupants-(doubles*2) < 0) ? 0 : occupants-(doubles*2);
				
				int mountain = randomGenerate(1, 0);
				
				int ocean = mountain == 1 ? 0 : randomGenerate(1,0);

				int balcony = randomGenerate(1,0);
				
				double price = 50 + ((balcony == 1) ? 20 : 0) + ((ocean == 1) ? 10 : 0) + ((mountain == 1) ? 10 : 0) + (occupants * 5);

				try {
					
					st.executeUpdate("INSERT INTO Rooms "
							+ "VALUES (" + roomNumber + ", " + occupants + ", " + singles + ", " + doubles + ", " + price + ", " + mountain + ", " + ocean + ", " + balcony + ")");

				}
				 
				catch(Exception e)
				{
					//System.out.println("Already Existing");
				}
			}		
		}

	}


	private static int randomGenerate(int max, int min) {

		Random random = new Random();

		int randomNumber = random.nextInt((max+1) - min) + min;
		
		return randomNumber;
	}

	
}
