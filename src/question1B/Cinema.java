package question1B;
import java.util.LinkedList;
import java.lang.Thread;
import java.util.Random;
import java.util.Queue;
import java.util.concurrent.Semaphore;
//import java.util.Arrays;

class Clerk implements Runnable {
	Client client;
	
	public int clerkNumber;
	
	public Clerk(int number) {
		clerkNumber = number;
	}
	
	public void run() {
		while (Cinema.isRunning == true) {
			helpClient();
		}
	}
	
	private synchronized boolean bookSeat(int seatNumber) {
		boolean seatStatus = Cinema.seats[seatNumber];
		if (seatStatus == false) {
			Cinema.seats[seatNumber] = true;
			client.booked = true;
			Cinema.seatsBooked += 1;
			return true;
		} else {
			System.out.println("Seat " + client.seatNumber + " is already taken!");
			client.orderSeat();
			return false;
		}
	}
	
	public void helpClient() {
		try {
			Cinema.numberOfClients.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		client = Cinema.clientQueue.remove();
		System.out.println("Clerk " + clerkNumber + " now helping client " + client.clientNumber);
		
		do {
			client.booked = bookSeat(client.seatNumber);
		} while (client.booked == false);
		
		try {
			Thread.sleep(Cinema.randomElement.nextInt(3)+1 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Client " + client.clientNumber + " successfully booked seat " + client.seatNumber + "\n");
		
		boolean seatsOpen = false;
		for (int i = 0; i < Cinema.seats.length ; i++) {
			if (Cinema.seats[i] == false) {
				seatsOpen = true;
			}
		}
		
		if (seatsOpen == false) {
			System.out.println("Clerk " + clerkNumber + " finished all orders.");
			Cinema.isRunning = false;
		}
	}
	
	public void begin() {
		(new Thread(new Clerk(this.clerkNumber))).start();
		System.out.println("Clerk number " + clerkNumber + " ready to help!");
	}
}

class Client implements Runnable {
	boolean booked = false;
	
	public int clientNumber;
	public int seatNumber;
	
	public Client(int number) {
		clientNumber = number;
	}

	@Override
	public void run() {
		orderSeat();
		Cinema.clientQueue.add(this);
		Cinema.numberOfClients.release();
	}
	
	public void  orderSeat() {
		seatNumber = Cinema.randomElement.nextInt(Cinema.numberOfSeats);	
		System.out.println("Client " + clientNumber + " order seat number " + seatNumber);
	}
	
	public void begin() {
		(new Thread(new Client(this.clientNumber))).start();
		System.out.println("Client number " + clientNumber + " arrived in queue.");
	}

}

public class Cinema {
	public static Queue<Client> clientQueue = new LinkedList<Client>();
	public static Semaphore numberOfClients;
	public static int numberOfClerks = 2;
	public static int helping;
	public static int seatsBooked = 0;
	public static int numberOfSeats = 10;
	public static boolean[] seats = new boolean[numberOfSeats]; //false = not reserved
	public static Random randomElement = new Random();
	public static boolean isRunning = true;
	
	public Cinema() {
		numberOfClients = new Semaphore(0);
		helping = 0;
	}

	public static void main(String[] args) {
		for (int i = 0; i < numberOfSeats; i++) {
			seats[i] = false;
		}
		
		Cinema cinema = new Cinema();
		
		for (int i = 1; i <= numberOfClerks; i++) {
			Clerk clerk = new Clerk(i);
			clerk.begin();
		}
		
		int clientNumber = 1;
		while (clientNumber <= numberOfSeats) {
			int i = randomElement.nextInt(4);
			if (i == 2) {
				Client client = new Client(clientNumber++);
				client.begin();	
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
