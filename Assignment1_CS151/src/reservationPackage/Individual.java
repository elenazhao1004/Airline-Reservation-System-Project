package reservationPackage;

public class Individual {
	private String name;
	private char preference;
	private boolean isEconomy;
	private int row, col;
	
	public Individual(String name, boolean isEconomy, char preference) {
		this.name = name;
		this.preference = preference;
		this.isEconomy = isEconomy;
		this.row = -1;
		this.col = -1;
	}
	
	//paras consists of [P, name, service, seatLabel]. (e.g.[P, John, Economy, 1A])
	public Individual(String[] paras) {
		this.name = paras[1];
		this.isEconomy = paras[2].equals("Economy");
		this.row = Integer.valueOf(paras[3].substring(0, paras[3].length() - 1)) - 1;
		this.col = paras[3].charAt(paras[3].length() - 1) - 'A';
	}
	
	public void sit(Plane plane) {
		Seat[][] chart = plane.getChart(isEconomy);
		chart[row][col].setIsTaken(true);
		chart[row][col].setPassengerName(name);
		plane.setRest(isEconomy, plane.getRest(isEconomy) - 1);
	}
	
	public boolean reserve(Plane plane) {
		int rest = plane.getRest(isEconomy);
		if (rest == 0) {
			return false;
		} else {
			Seat[][] chart = plane.getChart(isEconomy);
			for (int i = 0; i < chart.length; i++) {
				for (int j = 0; j < chart[0].length; j++) {
					if (!chart[i][j].getIsTaken() && chart[i][j].getPref() == preference) {
						row = i;
						col = j;
						chart[i][j].setIsTaken(true);
						chart[i][j].setPassengerName(name);
						plane.setRest(isEconomy, rest - 1);
						return true;
					}
				}
			}
			return true;
		}			
	}
	
	public boolean cancel(Plane plane) {
		if (row == -1 && col == -1) {
			return false;
		} else {
			plane.getChart(isEconomy)[row][col].setIsTaken(false);
			plane.getChart(isEconomy)[row][col].setPassengerName("");
			plane.setRest(isEconomy, plane.getRest(isEconomy) + 1);
			row = -1;
			col = -1;
			return true;
		}
	}
	
	public char getLabel() {
		return (char)(col + 'A');
	}
	
	public int getRow() {
		return row + 1;
	}
	
	public String getName() {
		return name;
	}
	
	public char getPreference() {
		return preference;
	}
	
	public String getService() {
		return isEconomy ? "Economy" : "First";
	}
}