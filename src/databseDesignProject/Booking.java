package databseDesignProject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.Interval;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Booking extends Application{

	private static Statement st;

	/**
	 * @return the st
	 */
	public static Statement getSt() {
		return st;
	}

	/**
	 * @param st the st to set
	 */
	public static void setSt(Statement st) {
		Booking.st = st;
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {

		primaryStage.setResizable(false);
		primaryStage.setTitle("Play to win!");

		VBox root = new VBox();
		Scene scene = new Scene(root, 600, 300);

		primaryStage.setScene(scene);

		HBox buttons = new HBox();

		Button enter = new Button("Enter");

		buttons.getChildren().add(enter);

		root.setAlignment(Pos.CENTER);
		buttons.setAlignment(Pos.CENTER);
		root.setSpacing(20);
		buttons.setSpacing(30);

		HBox textFields = new HBox();

		ComboBox<Integer> guests = new ComboBox<Integer>();
		DatePicker checkIn = new DatePicker();
		DatePicker checkOut = new DatePicker();

		guests.setItems(FXCollections.observableList(Arrays.asList(new Integer[] {1,2,3,4,5,6})));

		guests.setValue(1);

		checkIn.setOnAction(e ->
		{
			LocalDate date = checkIn.getValue();
			System.out.println("Selected date: " + date);

		});

		checkOut.setOnAction(e ->
		{
			LocalDate date = checkOut.getValue();
			System.out.println("Selected date: " + date);

		});

		//ResultSet rs = st.executeQuery("SELECT * FROM Rooms JOIN Reservation r ON r.roomNo = Rooms.roomNo "
		//	+ "WHERE maximumOccupancy = " + guests.getValue());


		List<Room> availableRooms = new ArrayList<Room>();




		Label guestNo = new Label("Number of Guests");
		Label arrival = new Label("Check in Date");
		Label departure = new Label("Check Out Date");

		VBox part1 = new VBox();
		VBox part2 = new VBox();
		VBox part3 = new VBox();

		part1.getChildren().addAll(guestNo, guests);
		part2.getChildren().addAll(arrival, checkIn);
		part3.getChildren().addAll(departure, checkOut);

		textFields.getChildren().addAll(part1, part2, part3);
		textFields.setSpacing(30);

		root.getChildren().addAll(textFields, buttons );

		textFields.setAlignment(Pos.CENTER);

		HBox root2 = new HBox();
		Scene scene2 = new Scene(root2, 640, 400);
		
		Button select = new Button("Select");

		enter.setOnAction(e ->
		{
			primaryStage.setScene(scene2);

			try {

				ResultSet rs3 = st.executeQuery("SELECT * FROM Rooms JOIN Reservation r ON "
						+ "Rooms.roomNo = r.roomNo "
						+ "WHERE maximumOccupancy = " + guests.getValue() + " "
						+ "AND (checkInDate NOT BETWEEN '" + (LocalDate) checkIn.getValue() + "' AND '" + (LocalDate) checkOut.getValue() + "') "
						+ "AND (checkOutDate NOT BETWEEN '" +  (LocalDate) checkIn.getValue() + "' AND '" + (LocalDate) checkOut.getValue() + "') ");

				while(rs3.next())
				{
					availableRooms.add(new Room(rs3.getInt("roomNo"), rs3.getInt("noOfSingles"), rs3.getInt("noOfDoubles"), 
							rs3.getBoolean("oceanView"), rs3.getBoolean("mountainView"), rs3.getBoolean("balcony"), rs3.getDouble("price")));
				}

				ResultSet rs2 = st.executeQuery("SELECT * FROM Rooms " 
						+ "HAVING Rooms.roomNo NOT IN " 
						+ "(SELECT roomNo FROM Reservation r)");



				while(rs2.next())
				{
					availableRooms.add(new Room(rs2.getInt("roomNo"), rs2.getInt("noOfSingles"), rs2.getInt("noOfDoubles"), 
							rs2.getBoolean("oceanView"), rs2.getBoolean("mountainView"), rs2.getBoolean("balcony"), rs2.getDouble("price")));
				}

				for(Room room : availableRooms)
					System.out.println(room.toString());
			}

			catch(Exception f)
			{
				f.printStackTrace();
			}
			
			TableView<Integer> details = new TableView<Integer>();
			
			TableColumn<Integer, Number> singleBeds = new TableColumn<>("Number of Single Beds");
			TableColumn<Integer, Number> doubleBeds = new TableColumn<>("Number of Double Beds");
			TableColumn<Integer, String> pricing = new TableColumn<>("Total Price");
			TableColumn<Integer, String> mView = new TableColumn<>("Mountain View");
			TableColumn<Integer, String> oView = new TableColumn<>("Ocean View");
			TableColumn<Integer, String> balconyView = new TableColumn<>("Balcony");
			

			List<Integer> s = new ArrayList<Integer>();
			List<Integer> s2 = new ArrayList<Integer>();
			List<String> s3 = new ArrayList<String>();
			List<String> s4 = new ArrayList<String>();
			List<String> s5 = new ArrayList<String>();
			List<String> s6 = new ArrayList<String>();
			
			
						
			int i = 0;
			for(Room r : availableRooms)
			{
				s.add(r.getNumberOfSingles());
				s2.add(r.getNumberOfDoubles());
				s3.add(String.valueOf(r.getPrice()));
				s4.add(String.valueOf(r.isMountainView()));
				s5.add(String.valueOf(r.isOceanView()));
				s6.add(String.valueOf(r.isBalcony()));

				details.getItems().add(i);
				i++;
			}
			
			
			
	        singleBeds.setCellValueFactory(cellData -> {
	            Integer rowIndex = cellData.getValue();
	            return new ReadOnlyIntegerWrapper(s.get(rowIndex));
	        });
	        doubleBeds.setCellValueFactory(cellData -> {
	            Integer rowIndex = cellData.getValue();
	            return new ReadOnlyIntegerWrapper(s2.get(rowIndex));
	        });
	        pricing.setCellValueFactory(cellData -> {
	            Integer rowIndex = cellData.getValue();
	            return new ReadOnlyStringWrapper(s3.get(rowIndex));
	        });
	        mView.setCellValueFactory(cellData -> {
	            Integer rowIndex = cellData.getValue();
	            return new ReadOnlyStringWrapper((s4.get(rowIndex).equalsIgnoreCase("true")) ? "✓" : "x");
	        });
	        oView.setCellValueFactory(cellData -> {
	            Integer rowIndex = cellData.getValue();
	            return new ReadOnlyStringWrapper((s5.get(rowIndex).equalsIgnoreCase("true")) ? "✓" : "x");
	        });
	        balconyView.setCellValueFactory(cellData -> {
	            Integer rowIndex = cellData.getValue();
	            return new ReadOnlyStringWrapper((s6.get(rowIndex).equalsIgnoreCase("true")) ? "✓" : "x");
	        });
	        
	        
			details.getColumns().add(singleBeds);
			details.getColumns().add(doubleBeds);
			details.getColumns().add(pricing);
			details.getColumns().add(mView);
			details.getColumns().add(oView);
			details.getColumns().add(balconyView);
			
			details.setPrefSize(800, 400);
			details.setPadding(new Insets(0, 0, 0, 10));
			
			VBox page2 = new VBox();
			page2.setAlignment(Pos.CENTER);
			page2.getChildren().addAll(details,select);
			page2.setSpacing(20);
			
			select.setPadding(new Insets(5, 5, 5, 5));
			
			root2.getChildren().add(page2);
			root2.setAlignment(Pos.CENTER);

		});
		
		HBox root3 = new HBox();
		Scene scene3 = new Scene(root3, 640, 400);

		select.setOnAction(e -> 
		{
			primaryStage.setScene(scene3);
			
			TextField firstName = new TextField();
			TextField surName = new TextField();
			TextField telNo = new TextField();
			
			VBox name = new VBox();
			VBox lastName = new VBox();
			VBox number = new VBox();
			
			name.setAlignment(Pos.CENTER);
			lastName.setAlignment(Pos.CENTER);
			number.setAlignment(Pos.CENTER);
			
			Label fName = new Label("First Name");
			Label sName = new Label("Last Name");
			Label telephone = new Label("Telephone Number");
			
			name.getChildren().addAll(fName, firstName);
			lastName.getChildren().addAll(sName, surName);
			number.getChildren().addAll(telephone, telNo);
		
			root3.getChildren().addAll(name, lastName, number);
			root3.setAlignment(Pos.CENTER);
			root3.setSpacing(20);

		});
		
		primaryStage.show();
	}

	public static void launchGUI(String[] args)
	{
		launch(args);
	}

	class Room 
	{
		private int roomNumber;
		private int numberOfSingles;
		private int numberOfDoubles;
		private boolean oceanView;
		private boolean mountainView;
		private boolean balcony;
		private double price;

		public Room( int roomNumber, int numberOfSingles, int numberOfDoubles, boolean oceanView, boolean mountainView, boolean balcony, double price)
		{
			this.roomNumber = roomNumber;
			this.numberOfSingles = numberOfSingles;
			this.numberOfDoubles = numberOfDoubles;
			this.oceanView = oceanView;
			this.mountainView = mountainView;
			this.balcony = balcony;
			this.price = price;
		}

		public String toString()
		{
			return roomNumber + " " + numberOfSingles + " " + numberOfDoubles + " ";
		}

		/**
		 * @return the roomNumber
		 */
		public int getRoomNumber() {
			return roomNumber;
		}

		/**
		 * @param roomNumber the roomNumber to set
		 */
		public void setRoomNumber(int roomNumber) {
			this.roomNumber = roomNumber;
		}

		/**
		 * @return the numberOfSingles
		 */
		public int getNumberOfSingles() {
			return numberOfSingles;
		}

		/**
		 * @param numberOfSingles the numberOfSingles to set
		 */
		public void setNumberOfSingles(int numberOfSingles) {
			this.numberOfSingles = numberOfSingles;
		}

		/**
		 * @return the numberOfDoubles
		 */
		public int getNumberOfDoubles() {
			return numberOfDoubles;
		}

		/**
		 * @param numberOfDoubles the numberOfDoubles to set
		 */
		public void setNumberOfDoubles(int numberOfDoubles) {
			this.numberOfDoubles = numberOfDoubles;
		}

		/**
		 * @return the oceanView
		 */
		public boolean isOceanView() {
			return oceanView;
		}

		/**
		 * @param oceanView the oceanView to set
		 */
		public void setOceanView(boolean oceanView) {
			this.oceanView = oceanView;
		}

		/**
		 * @return the mountainView
		 */
		public boolean isMountainView() {
			return mountainView;
		}

		/**
		 * @param mountainView the mountainView to set
		 */
		public void setMountainView(boolean mountainView) {
			this.mountainView = mountainView;
		}

		/**
		 * @return the balcony
		 */
		public boolean isBalcony() {
			return balcony;
		}

		/**
		 * @param balcony the balcony to set
		 */
		public void setBalcony(boolean balcony) {
			this.balcony = balcony;
		}

		/**
		 * @return the price
		 */
		public double getPrice() {
			return price;
		}

		/**
		 * @param price the price to set
		 */
		public void setPrice(double price) {
			this.price = price;
		}
	}

}
