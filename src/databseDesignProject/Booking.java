package databseDesignProject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Booking extends Application{

	private static Statement st;
	TableView<Room> details = new TableView<Room>();
	DatePicker checkIn = new DatePicker();
	DatePicker checkOut = new DatePicker();
	ComboBox<Integer> guests = new ComboBox<Integer>();
	TextField firstName = new TextField();
	TextField surName = new TextField();
	TextField telNo = new TextField();
	ComboBox<String> paymentChoice = new ComboBox<String>();
	TextField numberOnCard = new TextField();

	double finalCost = 0;


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
		Scene scene = new Scene(root, 700, 300);

		primaryStage.setScene(scene);

		HBox buttons = new HBox();

		Button enter = new Button("Enter");

		buttons.getChildren().add(enter);

		root.setAlignment(Pos.CENTER);
		buttons.setAlignment(Pos.CENTER);
		root.setSpacing(20);
		buttons.setSpacing(30);

		HBox textFields = new HBox();



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


			try {

				if(checkIn.getValue() == null || checkOut.getValue() == null)
					return;



				int noOfDays = checkOut.getValue().compareTo(checkIn.getValue());

				LocalDate currentDay = LocalDate.now();
				if(checkIn.getValue().compareTo(currentDay) <= 0 || checkOut.getValue().compareTo(currentDay) <= 0 || noOfDays <= 0)
				{
					Alert alertBox = new Alert(AlertType.ERROR);

					alertBox.setContentText("Invalid dates");
					alertBox.setHeaderText("ERROR");

					alertBox.show();

					return;
				}

				ResultSet rs3 = st.executeQuery("SELECT * FROM Rooms JOIN Reservation r ON "
						+ "Rooms.roomNo = r.roomNo "
						+ "WHERE maximumOccupancy = '" + guests.getValue() + "' "
						+ "AND (checkInDate NOT BETWEEN '" + (LocalDate) checkIn.getValue() + "' AND '" + (LocalDate) checkOut.getValue() + "') "
						+ "AND (checkOutDate NOT BETWEEN '" +  (LocalDate) checkIn.getValue() + "' AND '" + (LocalDate) checkOut.getValue() + "') ");

				primaryStage.setScene(scene2);
				System.out.println();
				
				
				while(rs3.next())
				{
					availableRooms.add(new Room(rs3.getInt("roomNo"), rs3.getInt("noOfSingles"), rs3.getInt("noOfDoubles"), 
							rs3.getBoolean("oceanView"), rs3.getBoolean("mountainView"), rs3.getBoolean("balcony"), rs3.getDouble("price")));
				}

				ResultSet rs2 = st.executeQuery("SELECT * FROM Rooms "
						+ "WHERE maximumOccupancy = '" +guests.getValue() + "' " 
						+ "HAVING Rooms.roomNo NOT IN " 
						+ "(SELECT roomNo FROM Reservation r)");



				while(rs2.next())
				{
					availableRooms.add(new Room(rs2.getInt("roomNo"), rs2.getInt("noOfSingles"), rs2.getInt("noOfDoubles"), 
							rs2.getBoolean("oceanView"), rs2.getBoolean("mountainView"), rs2.getBoolean("balcony"), rs2.getDouble("price")));

					
					
					finalCost = (rs2.getDouble("price") * noOfDays);
				}
			}

			catch(Exception f)
			{
				f.printStackTrace();
			}



			TableColumn<Room, Integer> singleBeds = new TableColumn<>("Number of Single Beds");
			TableColumn<Room, Integer> doubleBeds = new TableColumn<>("Number of Double Beds");
			TableColumn<Room, Double> pricing = new TableColumn<>("Price Per Night");
			TableColumn<Room, String> mView = new TableColumn<>("Mountain View");
			TableColumn<Room, String> oView = new TableColumn<>("Ocean View");
			TableColumn<Room, String> balconyView = new TableColumn<>("Balcony");

			singleBeds.setCellValueFactory(new PropertyValueFactory<Room, Integer>("numberOfSingles"));
			doubleBeds.setCellValueFactory(new PropertyValueFactory<Room, Integer>("numberOfDoubles"));
			pricing.setCellValueFactory(new PropertyValueFactory<Room, Double>("price"));
			//mView.setCellValueFactory(new PropertyValueFactory<Room, String>("mView"));
			//oView.setCellValueFactory(new PropertyValueFactory<Room, String>("oView"));
			//balconyView.setCellValueFactory(new PropertyValueFactory<Room, String>("bView"));
			
			mView.setCellValueFactory(
				      cellData -> cellData.getValue().mView);
			
			mView.setCellFactory(new Callback<TableColumn<Room, String>, TableCell<Room, String>>(){

				@Override
				public TableCell<Room, String> call(TableColumn<Room, String> arg0) {

					TableCell<Room, String> tc = new TableCell<Room, String>(){

						public void updateItem(String item, boolean empty)
						{
							super.updateItem(item,empty);
							if(item != null)
								setText(item);
						}
					};
					return tc;
				}
			});
			

			oView.setCellValueFactory(
				      cellData -> cellData.getValue().oView);
			
			oView.setCellFactory(new Callback<TableColumn<Room, String>, TableCell<Room, String>>(){

				@Override
				public TableCell<Room, String> call(TableColumn<Room, String> arg0) {

					TableCell<Room, String> tc = new TableCell<Room, String>(){

						public void updateItem(String item, boolean empty)
						{
							super.updateItem(item,empty);
							if(item != null)
								setText(item);
						}
					};
					return tc;
				}
			});


			balconyView.setCellValueFactory(
				      cellData -> cellData.getValue().bView);
			
			balconyView.setCellFactory(new Callback<TableColumn<Room, String>, TableCell<Room, String>>(){

				@Override
				public TableCell<Room, String> call(TableColumn<Room, String> arg0) {

					TableCell<Room, String> tc = new TableCell<Room, String>(){

						public void updateItem(String item, boolean empty)
						{
							super.updateItem(item,empty);
							if(item != null)
								setText(item);
						}
					};
					return tc;
				}
			});
			
			details.setItems(FXCollections.observableList(availableRooms));
			
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

		VBox root3 = new VBox();
		Scene scene3 = new Scene(root3, 700, 400);

		Button confirm = new Button("Confirm");

		select.setOnAction(e -> 
		{
			primaryStage.setScene(scene3);

			TextArea totalCost = new TextArea();

			totalCost.setPrefSize(50, 50);


			totalCost.setText(String.valueOf(finalCost));

			HBox name = new HBox();
			HBox lastName = new HBox();
			HBox number = new HBox();

			HBox payment = new HBox();
			HBox cardNo = new HBox();
			HBox cost = new HBox();
			
			paymentChoice.getItems().addAll("Visa", "Debit", "Credit");

			name.setSpacing(20);
			lastName.setSpacing(20);
			number.setSpacing(20);
			payment.setSpacing(20);
			cardNo.setSpacing(20);
			cost.setSpacing(20);

			name.setAlignment(Pos.CENTER);
			lastName.setAlignment(Pos.CENTER);
			number.setAlignment(Pos.CENTER);
			payment.setAlignment(Pos.CENTER);
			cardNo.setAlignment(Pos.CENTER);
			cost.setAlignment(Pos.CENTER);

			Label fName = new Label("First Name");
			Label sName = new Label("Last Name");
			Label telephone = new Label("Telephone Number");
			Label paymentMethod = new Label("Payment Method");
			Label cardNumber = new Label("Number on card");
			Label amount = new Label("Total amount: ");

			name.getChildren().addAll(fName, firstName);
			lastName.getChildren().addAll(sName, surName);
			number.getChildren().addAll(telephone, telNo);

			payment.getChildren().addAll(paymentMethod, paymentChoice);
			cardNo.getChildren().addAll(cardNumber, numberOnCard);
			cost.getChildren().addAll(amount, totalCost);

			root3.getChildren().addAll(name, lastName, number, payment, cardNo, cost);
			root3.setAlignment(Pos.CENTER);
			root3.setSpacing(20);

			confirm.setAlignment(Pos.CENTER);

			root3.getChildren().add(confirm);
			


		});

		HBox root4 = new HBox();
		Scene scene4 = new Scene(root4, 640, 400);

		confirm.setOnAction(e ->
		{
			primaryStage.setScene(scene4);

			Room selectedRoom = details.getSelectionModel().getSelectedItem();

			int custID = 0;

			try {

				st.executeUpdate("INSERT INTO CustomerDetails (firstName, surName, telno) "
						+ "VALUES ('" + firstName.getText() + "', '" + surName.getText() + "', " + telNo.getText() + ")");

				ResultSet rs5 = st.executeQuery("SELECT customerID FROM CustomerDetails "
						+ "WHERE firstName = '" + firstName.getText() + "'"
						+ "AND surName = '" + surName.getText() + "' "
						+ "AND telNo = " + telNo.getText() + "");

				if(rs5.next())
				{
					custID = rs5.getInt("customerID");
				}

				st.executeUpdate("INSERT INTO Reservation "
						+ "VALUES (" + selectedRoom.getRoomNumber() + ", '" + checkIn.getValue() + "', '" + checkOut.getValue() + "', " + guests.getValue() + ", " + custID + ")");



			} catch (SQLException e1) {

				e1.printStackTrace();
			}


			try {
				
				st.executeUpdate("INSERT INTO paymentDetails "
						+ "VALUES (" + custID + ", " + numberOnCard.getText() + ", '" + paymentChoice.getValue() + "', '" + finalCost + "')");
			}
			
			catch (SQLException e1) 
			{

				e1.printStackTrace();
			}

			Label confirmation = new Label("Your booking was successful");
			root4.getChildren().add(confirmation);

			root4.setAlignment(Pos.CENTER);

			TablePosition tp = details.getFocusModel().getFocusedCell();

			int tr = tp.getRow();
			TableColumn tc = tp.getTableColumn();

			String a = details.getColumns().get(0).getCellObservableValue(0).getValue().toString(); 
			Object object =  details.getSelectionModel().selectedItemProperty().get();
			int index = details.getSelectionModel().selectedIndexProperty().get(); 

			System.out.println(object);
			System.out.println(a);

		});

		primaryStage.show();
	}



	public static void launchGUI(String[] args)
	{
		launch(args);
	}

	public class Room 
	{
		private int roomNumber;
		private SimpleIntegerProperty numberOfSingles;
		private SimpleIntegerProperty numberOfDoubles;
		private SimpleBooleanProperty oceanView;
		private SimpleBooleanProperty mountainView;
		private SimpleBooleanProperty balcony;
		private SimpleDoubleProperty price;

		private SimpleStringProperty oView;
		private SimpleStringProperty mView;
		private SimpleStringProperty bView;


		private Room( int roomNumber, int numberOfSingles, int numberOfDoubles, boolean oceanView, boolean mountainView, boolean balcony, double price)
		{
			this.roomNumber = roomNumber;
			this.numberOfSingles = new SimpleIntegerProperty(numberOfSingles);
			this.numberOfDoubles = new SimpleIntegerProperty(numberOfDoubles);
			this.oceanView = new SimpleBooleanProperty(oceanView);
			this.mountainView = new SimpleBooleanProperty(mountainView);
			this.balcony = new SimpleBooleanProperty(balcony);
			this.price = new SimpleDoubleProperty(price);

			this.oView = (oceanView) ? new SimpleStringProperty("✓") : new SimpleStringProperty("x");
			this.mView = (mountainView) ? new SimpleStringProperty("✓") : new SimpleStringProperty("x");
			this.bView = (balcony) ? new SimpleStringProperty("✓") : new SimpleStringProperty("x");
		}

		public String toString()
		{
			return roomNumber + " " + numberOfSingles.get() + " " + numberOfDoubles.get() + " ";
		}

		/**
		 * @return the roomNumber
		 */
		public int getRoomNumber() {
			return roomNumber;
		}

		/**
		 * @return the numberOfSingles
		 */
		public int getNumberOfSingles() {
			return numberOfSingles.get();
		}

		/**
		 * @param numberOfSingles the numberOfSingles to set
		 */
		public void setNumberOfSingles(SimpleIntegerProperty numberOfSingles) {
			this.numberOfSingles.set(numberOfSingles.get());
		}

		/**
		 * @return the numberOfDoubles
		 */
		public int getNumberOfDoubles() {
			return numberOfDoubles.get();
		}

		/**
		 * @param numberOfDoubles the numberOfDoubles to set
		 */
		public void setNumberOfDoubles(SimpleIntegerProperty numberOfDoubles) {
			this.numberOfDoubles.set(numberOfDoubles.get());
		}

		/**
		 * @return the oceanView
		 */
		public boolean getOceanView() {
			return oceanView.get();
		}

		/**
		 * @param oceanView the oceanView to set
		 */
		public void setOceanView(SimpleBooleanProperty oceanView) {
			this.oceanView.set(oceanView.get());
		}

		/**
		 * @return the mountainView
		 */
		public boolean getMountainView() {
			return mountainView.get();
		}

		/**
		 * @param mountainView the mountainView to set
		 */
		public void setMountainView(SimpleBooleanProperty mountainView) {
			this.mountainView.set(mountainView.get());
		}

		/**
		 * @return the balcony
		 */
		public boolean getBalcony() {
			return balcony.get();
		}

		/**
		 * @param balcony the balcony to set
		 */
		public void setBalcony(SimpleBooleanProperty balcony) {
			this.balcony.set(balcony.get());
		}

		/**
		 * @return the price
		 */
		public double getPrice() {
			return price.get();
		}

		/**
		 * @param price the price to set
		 */
		public void setPrice(SimpleDoubleProperty price) {
			this.price.set(price.get());
		}

		/**
		 * @param roomNumber the roomNumber to set
		 */
		public void setRoomNumber(int roomNumber) {
			this.roomNumber = roomNumber;
		}

		/**
		 * @param roomNumber the roomNumber to set
		 */

		public String toString(int price)
		{
			return " " + price;
		}

		/**
		 * @return the oView
		 */
		public String getoView() {
			return oView.get();
		}

		/**
		 * @param oView the oView to set
		 */
		public void setoView(SimpleStringProperty oView) {
			this.oView.set(oView.get());
		}

		/**
		 * @return the mView
		 */
		public String getmView() {
			return mView.get();
		}

		/**
		 * @param mView the mView to set
		 */
		public void setmView(SimpleStringProperty mView) {
			this.mView.set(mView.get());
		}

		/**
		 * @return the bView
		 */
		public String getbView() {
			return bView.get();
		}

		/**
		 * @param bView the bView to set
		 */
		public void setbView(SimpleStringProperty bView) {
			this.bView.set(bView.get());
		}

	}

}
