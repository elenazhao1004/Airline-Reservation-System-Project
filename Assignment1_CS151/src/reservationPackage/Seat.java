package reservationPackage;

public class Seat{
	private boolean isTaken;
	private int row;
	private char label, pref;
	private String passengerName;
	public Seat(int row, int col, char label, char pref) {
		this.isTaken = false;
		this.row = row;
		this.label = label;
		this.pref = pref;
		this.passengerName = "";
	}
	
	public boolean getIsTaken() {
		return isTaken;
	}
	
	public char getLabel() {
		return label;
	}
	
	public String getPassengerName() {
		return passengerName;
	}
	
	public char getPref() {
		return pref;
	}
	
	public int getRow() {
		return row + 1;
	}
	
	public void setIsTaken(boolean val) {
		isTaken = val;
		return;
	}
	
	public void setPassengerName(String name) {
		passengerName = name;
		return;
	}
}