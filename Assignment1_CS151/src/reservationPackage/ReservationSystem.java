package reservationPackage;

import java.util.*;

public class ReservationSystem {
	
	public static void main(String[] args) {
		String filePath = args[2] + ".txt";		
		int firstRow = 2, firstCol = 4, ecoRow = 20, ecoCol = 6;
		Plane plane = new Plane(firstRow, firstCol, ecoRow, ecoCol);
		InitializeMethods.loadInfo(plane, filePath);
		InitializeMethods.initializePlane(plane);
		InitializeMethods.commandHandler(plane, filePath, new Scanner(System.in));
	}
	
}
