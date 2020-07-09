package reservationPackage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Plane {
	private Seat[][] firstChart, ecoChart;
	private int firstRest, ecoRest;
	private Map<String, Individual> passengerMap;
	private Map<String, Group> groupMap;
	private Set<Individual> passengers;
	private Set<Group> groups;
	
	public Plane(int firstRow, int firstCol, int ecoRow, int ecoCol) {
		this.firstChart = fillSeats(firstRow, firstCol);
		this.ecoChart = fillSeats(ecoRow, ecoCol);
		this.firstRest = firstRow * firstCol;
		this.ecoRest = ecoRow * ecoCol;
		this.passengerMap = new HashMap<String, Individual>();
		this.groupMap = new HashMap<String, Group>();
		this.passengers = new HashSet<Individual>();
		this.groups = new HashSet<Group>();
	}
	
	public void addGroup(Group group) {
		groupMap.put(group.getGroupName(), group);
		groups.add(group);
		return;
	}
	
	public void addPassenger(Individual passenger) {
		passengerMap.put(passenger.getName(), passenger);
		passengers.add(passenger);
		return;
	}
	
	public void removeGroup(Group group) {
		groupMap.remove(group.getGroupName());
		groups.remove(group);
		return;
	}
	
	public void removePassenger(Individual passenger) {
		passengerMap.remove(passenger.getName());
		passengers.remove(passenger);
		return;
	}
	
	public Seat[][] getChart(boolean isEconomy) {
		return isEconomy ? ecoChart : firstChart;
	}
	
	public Map<String, Group> getGroupMap() {
		return groupMap;
	}
	
	public Set<Group> getGroups() {
		return groups;
	}
	
	public Map<String, Individual> getPassengerMap() {
		return passengerMap;
	}
	
	public Set<Individual> getPassengers() {
		return passengers;
	}
	
	public int getRest(boolean isEconomy) {
		return isEconomy ? ecoRest : firstRest;
	}
	
	public void setRest(boolean isEconomy, int val) {
		if (isEconomy) {
			ecoRest = val;
		} else {
			firstRest = val;
		}
		return;
	}
	
	private Seat[][] fillSeats(int row, int col) {
		Seat[][] seats = new Seat[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				char label = (char)(j + 'A');
				if (j == 0 || j == col - 1) {
					seats[i][j] = new Seat(i, j, label, 'W');
				} else if (j == col / 2 - 1 || j == col / 2) {
					seats[i][j] = new Seat(i, j, label, 'A');
				} else {
					seats[i][j] = new Seat(i, j, label, 'C');
				}
			}
		}
		return seats;
	}
	
	public void printAvailableSeats(boolean isEconomy) {
		Seat[][] chart = getChart(isEconomy);
		System.out.print(isEconomy ? "Economy	" : "First	");
		for (int i = 0; i < chart.length; i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < chart[0].length; j++) {
				if (!chart[i][j].getIsTaken()) {
					sb.append(chart[i][j].getLabel()).append(", ");
				}
			}
			int sbLen = sb.length();
			if (sbLen > 0) {
				sb.replace(sbLen - 2, sbLen, "  ");
				System.out.print((i + 1) + ": ");
				System.out.print(sb.toString());
			}
		}
		System.out.println();
	}
	
	public void printManifest(boolean isEconomy) {
		Seat[][] chart = getChart(isEconomy);
		System.out.print(isEconomy ? "Economy	" : "First	");
		for (int i = 0; i < chart.length; i++) {
			for (int j = 0; j < chart[0].length; j++) {
				Seat curSeat = chart[i][j];
				if (curSeat.getIsTaken()) {
					System.out.print(" " + curSeat.getRow() + curSeat.getLabel() + ": " + curSeat.getPassengerName());
				}
			}
		}
		System.out.println();
	}
}