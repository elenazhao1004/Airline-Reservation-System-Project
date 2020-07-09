package reservationPackage;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class InitializeMethods {
	public static void loadInfo(Plane plane, String filePath) {
		try {
			FileReader fr = new FileReader(filePath);
			Scanner scanner = new Scanner(fr);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.length() == 0) {
					break;
				} else if (line.charAt(0) == 'P') {
					Individual passenger = new Individual(line.split("\\|"));
					passenger.sit(plane);
					plane.addPassenger(passenger);
				} else if (line.charAt(0) == 'G') {
					Group group = new Group(line.split("\\|"));
					group.sit(plane);
					plane.addGroup(group);
				} else {
					System.out.println("Input Error!!! Input file has wrong format, please double check.");
				}
			}
			scanner.close();
			fr.close();
		} catch (Exception e) {
			try {
				FileWriter fw = new FileWriter(filePath);
				fw.close();
			} catch (Exception unexpectedException) {
				System.out.println("Unexpected Exception: " + unexpectedException);
			}
		}
		return;
	}
	
	public static void initializePlane(Plane plane) {
		System.out.println("Add [P]assenger, Add [G]roup, [C]ancel Reservations, "
				+ "Print Seating [A]vailability Chart, Print [M]anifest, [Q]uit");
		System.out.println("************************************************************************************");
		System.out.println("Please don't try to assign a seat with label 'C' if it doesn't exist");
		System.out.println("************************************************************************************");
		System.out.println("P + passenger name + service class + preference-------------Add a passenger");
		System.out.println("G + group name + names seperated by comma + service class---Add a group");
		System.out.println("C + I + individual passenger name---------------------------Cancel for a passenger");
		System.out.println("C + G + group name------------------------------------------Cancel for a group");
		System.out.println("A + service class-------------------------------------------Print the available list");
		System.out.println("M + service class-------------------------------------------Print the manifest");
		System.out.println("Q-----------------------------------------------------------Quit program and save");
		System.out.println("************************************************************************************");
	}
	
	public static void commandHandler(Plane plane, String filePath, Scanner scanner) {
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			switch (line) {
				case "P":
					addPassenger(plane, scanner, 3);
					break;
				case "G":
					addGroup(plane, scanner, 3);
					break;
				case "C":
					cancel(plane, scanner, 2);
					break;
				case "A":
					availableChart(plane, scanner);
					break;
				case "M":
					manifest(plane, scanner);
					break;
				case "Q":
					quit(plane, filePath);
					break;
				default:
					System.out.println("Input Error!!! Please provide valid input (P/G/C/A/M/Q).");
			}
		}
		scanner.close();
	}
	
	private static void addPassenger(Plane plane, Scanner scanner, int steps){
		String[] parameters = new String[steps];
		for (int i = 0; i < steps; i++) {
			if (scanner.hasNextLine()) {
				parameters[i] = scanner.nextLine();
			}
		}
		if (isServiceValid(parameters[1]) && isPreferenceValid(parameters[2])) {
			boolean isEconomy = parameters[1].equals("First") ? false : true;
			char pref = parameters[2].charAt(0);
			Individual passenger = new Individual(parameters[0], isEconomy, pref);
			System.out.println("Name: " + passenger.getName() + ", Service Class: " + passenger.getService()
			+ ", Seat Preference: " + passenger.getPreference());
			if (passenger.reserve(plane)) {
				plane.addPassenger(passenger);
				System.out.println("" + passenger.getRow() + passenger.getLabel());
			} else {
				System.out.println("Individual reservation request failed: not enough seats available");
			}
		}
		return;
	}
	
	private static void addGroup(Plane plane, Scanner scanner, int steps){
		String[] parameters = new String[steps];
		for (int i = 0; i < steps; i++) {
			if (scanner.hasNextLine()) {
				parameters[i] = scanner.nextLine();
			}
		}
		if (isServiceValid(parameters[2])) {
			String[] namesArr = parameters[1].split(",");
			List<String> names = new ArrayList<>();
			for (String str : namesArr) {
				names.add(str.trim());
			}
			boolean isEconomy = parameters[2].equals("Economy") ? true : false;
			Group group = new Group(parameters[0], names, isEconomy);
			System.out.println("Group Name: " + group.getGroupName() + ", Names: " + group.getNames() + ", Service Class: " + 
								group.getService());
			if (group.reserve(plane)) {
				plane.addGroup(group);
				System.out.println(group.getIndices());
			} else {
				System.out.println("Group reservation failed: not enough seats available");
			}
		}
		return;
	}
	
	private static void cancel(Plane plane, Scanner scanner, int steps){
		System.out.println("Are you going to cancel the seat for an individual or a group? [I/G]");
		String[] parameters = new String[steps];
		for (int i = 0; i < steps; i++) {
			if (scanner.hasNextLine()) {
				parameters[i] = scanner.nextLine();
			}
		}
		if (parameters[0].equals("I")) {
			String name = parameters[1];
			if (plane.getPassengerMap().containsKey(name)) {
				plane.getPassengers().remove(plane.getPassengerMap().get(name));
				if (!plane.getPassengerMap().get(name).cancel(plane)) {
					System.out.println("Cancellation failed! This passenger has already canceled the seat.");
				}
			} else {
				System.out.println("Input Error!!! No such passenger found");
			}
		} else if (parameters[0].equals("G")) {
			String groupName = parameters[1];
			if (plane.getGroupMap().containsKey(groupName)) {
				plane.getGroups().remove(plane.getGroupMap().get(groupName));
				if (!plane.getGroupMap().get(groupName).cancel(plane)) {
					System.out.println("Cancellation failed! This group has already canceled the seats.");
				}
			} else {
				System.out.println("Input Error!!! No such group found");
			}
		} else {
			System.out.println("Input Error!!! Please input 'I' for individual or 'G' for group");
		}
	}

	private static void availableChart(Plane plane, Scanner scanner){
		System.out.println("Which service class do you want to print? [First/Economy]");
		if (scanner.hasNextLine()) {
			String service = scanner.nextLine();
			if (isServiceValid(service)) {
				boolean isEconomy = service.equals("Economy") ? true : false;
				plane.printAvailableSeats(isEconomy);
			}
		}
		return;
	}

	private static void manifest(Plane plane, Scanner scanner){
		System.out.println("Which service class do you want to print? [First/Economy]");
		if (scanner.hasNextLine()) {
			String service = scanner.nextLine();
			if (isServiceValid(service)) {
				boolean isEconomy = service.equals("Economy") ? true : false;
				plane.printManifest(isEconomy);
			}
		}
		return;
	}

	private static void quit(Plane plane, String filePath) {
		try {
			FileWriter fw = new FileWriter(filePath);
			Set<Individual> passengers = plane.getPassengers();
			Set<Group> groups = plane.getGroups();
			for (Individual passenger : passengers) {
				fw.write("P|" + passenger.getName() + "|" + passenger.getService() + "|" + passenger.getRow() + passenger.getLabel() + "\n");
			}
			for (Group group : groups) {
				fw.write("G|" + group.getGroupName() + "|" + group.getNames() + "|" + group.getIndices() + "|" + group.getService() + "\n");
			}
			fw.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		System.exit(0);
	}
	
	private static boolean isServiceValid(String str) {
		if (str.equals("First") || str.equals("Economy")) {
			return true;
		} else {
			System.out.println("Input Error!!! Service class is not valid");
			return false;
		}
	}
	
	private static boolean isPreferenceValid(String str) {
		if (str.equals("W") || str.equals("A") || str.equals("C")) {
			return true;
		} else {
			System.out.println("Input Error!!! Seat preference is not valid");
			return false;
		}
	}
}
