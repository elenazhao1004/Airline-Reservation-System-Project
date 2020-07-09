package reservationPackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Group {
	private String groupName;
	private List<String> names;
	private List<int[]> indices;
	private boolean isEconomy;
	
	public Group(String groupName, List<String> names, boolean isEconomy) {
		this.groupName = groupName;
		this.names = names;
		this.indices = new ArrayList<int[]>();
		this.isEconomy = isEconomy;
	}
	
	//paras consists of [G, group name, names (in String format, separated by comma), 
	//seat labels (in String format, separated by comma), service]
	public Group(String[] paras) {
		this.groupName = paras[1];
		this.names = new ArrayList<>();
		for (String str : paras[2].split(",")) {
			names.add(str.trim());
		}
		this.indices = new ArrayList<int[]>();
		for (String str : paras[3].split(",")) {
			String strCpy = str.trim();
			int row = Integer.valueOf(strCpy.substring(0, strCpy.length() - 1)) - 1;
			int col = strCpy.charAt(strCpy.length() - 1) - 'A';
			this.indices.add(new int[]{row, col});
		}
		this.isEconomy = paras[4].equals("Economy");
	}
	
	public void sit(Plane plane) {
		Seat[][] chart = plane.getChart(isEconomy);
		plane.setRest(isEconomy, plane.getRest(isEconomy) - names.size());
		for (int i = 0; i < names.size(); i++) {
			int row = indices.get(i)[0], col = indices.get(i)[1];
			chart[row][col].setIsTaken(true);
			chart[row][col].setPassengerName(names.get(i));
		}
	}
	
	public boolean reserve(Plane plane) {
		Seat[][] chart = plane.getChart(isEconomy);
		int rest = plane.getRest(isEconomy);
		if (rest < names.size()) {
			return false;
		} else {
			plane.setRest(isEconomy, rest - names.size());
		}
		
		/* Try to find a row with consecutive seats that can place all passengers in this group.
		   If not able to find such a line, find out all the consecutive seats and place as many
		   passengers as possible in one time */
		int needed = names.size();
		List<List<int[]>> emptySlots = new ArrayList<>(); // each slot records the starting empty seat and the number of adjacent empty seats
		for (int i = 0; i < chart.length; i++) {
			emptySlots.add(new ArrayList<>());
			int consecutiveSeats = 0;
			for (int j = 0; j < chart[0].length; j++) {
				if (!chart[i][j].getIsTaken()) {
					consecutiveSeats++;
					if (consecutiveSeats == needed) {
						for (int k = needed - 1; k >= 0; k--) {
							indices.add(new int[]{i, j - k});
							chart[i][j-k].setIsTaken(true);
							chart[i][j-k].setPassengerName(names.get(needed - 1 - k));
						}
						return true;
					}
				}
				
				if (consecutiveSeats > 0 && (j == chart[0].length - 1 || chart[i][j].getIsTaken())) {
					if (chart[i][j].getIsTaken()) {
						emptySlots.get(i).add(new int[]{j - consecutiveSeats, consecutiveSeats});
					} else {
						emptySlots.get(i).add(new int[]{j + 1 - consecutiveSeats, consecutiveSeats});
					}
					consecutiveSeats = 0;
				}
			}
			
		}
		findMaxAndAssign(emptySlots, chart, indices, needed, 0);
		return true;
	}
	
	// assign the largest number of adjacent seats if unable to assign all seats in a row
	private void findMaxAndAssign(List<List<int[]>> emptySlots, Seat[][] chart, List<int[]> indices, int needed, int nextPassengerIdx) {
		int max = 0, startRow = -1, startCol = -1, removeIndex = -1;
		for (int i = 0; i < emptySlots.size(); i++) {
			for (int j = 0; j < emptySlots.get(i).size(); j++) {
				int[] slot = emptySlots.get(i).get(j);
				if (slot[1] >= needed) {
					assign(chart, indices, needed, i, slot[0], nextPassengerIdx);
					removeSlot(emptySlots.get(i), j, slot[1] - needed);
					return;
				} else {
					if (slot[1] > max) {
						max = slot[1];
						startRow = i;
						startCol = slot[0];
						removeIndex = j;
					}
				}
			}
		}
		assign(chart, indices, max, startRow, startCol, nextPassengerIdx);
		removeSlot(emptySlots.get(startRow), removeIndex, 0);
		findMaxAndAssign(emptySlots, chart, indices, needed - max, nextPassengerIdx + max);
		return;
	}
	
	// assign seats to passengers, 
	private void assign(Seat[][] chart, List<int[]> indices, int arrange, int row, int startCol, int nextPassengerIdx) {
		for (int col = startCol; col < startCol + arrange; col++) {
			chart[row][col].setIsTaken(true);
			chart[row][col].setPassengerName(names.get(nextPassengerIdx++));
			indices.add(new int[]{row, col});
		}
		return;
	}
	
	//remove the toRemoveSlot and update if necessary
	private void removeSlot(List<int[]> slotList, int removeIndex, int restSize) {
		if (restSize > 0) {
			int[] toRemove = slotList.get(removeIndex);
			int[] restList = Arrays.copyOfRange(toRemove, toRemove.length - restSize, toRemove.length);
			slotList.remove(removeIndex);
			slotList.add(removeIndex, restList);
		} else {
			slotList.remove(removeIndex);
		}
		return;
	}
	
	public boolean cancel(Plane plane) {
		if (indices.isEmpty()) {
			return false;
		} else {
			Seat[][] chart = plane.getChart(isEconomy);
			plane.setRest(isEconomy, plane.getRest(isEconomy) + names.size());
			for (int[] index : indices) {
				chart[index[0]][index[1]].setIsTaken(false);
				chart[index[0]][index[1]].setPassengerName("");
			}
			indices.clear();
			return true;
		}
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public String getIndices() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < indices.size(); i++) {
			int[] index = indices.get(i);
			sb.append((index[0] + 1) + "" + (char)(index[1] + 'A'));
			if (i < indices.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString().trim();
	}
	
	public String getNames() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < names.size(); i++) {
			sb.append(names.get(i));
			if (i < names.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString().trim();
	}
	
	public String getService() {
		return isEconomy ? "Economy" : "First";
	}
	
}