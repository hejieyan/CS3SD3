package question3;
import java.lang.Thread;

class ArrivingProcess implements Runnable {
	int processID;
	Barrier barrier;
	
	public ArrivingProcess(int processID, Barrier barrier) {
		this.processID = processID;
		this.barrier = barrier;
	}
	
	public void run() {
		System.out.println("Process " + processID + " arriving at barrier." );
		barrier.arrive();
	}
	
	public void begin() {
		(new Thread(new ArrivingProcess(this.processID, this.barrier))).start();
	}
}

class DepartingProcess implements Runnable {
	int processID;
	Barrier barrier;
	
	public DepartingProcess(int processID, Barrier barrier) {
		this.processID = processID;
		this.barrier = barrier;
	}
	
	public void run() {
		try {
			while(true) {
				barrier.depart();	
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void begin() {
		(new Thread(new DepartingProcess(this.processID, this.barrier))).start();
	}
}

public class Barrier {
	static int numberOfProcesses;
	static int processInBarrier;
	

	public Barrier() {
		numberOfProcesses = 10;
		processInBarrier = 0;
	}
	
	synchronized void arrive() {
		processInBarrier += 1;
		System.out.println("Need " + (numberOfProcesses - processInBarrier) + " more processes to arrive.");
		notifyAll();
	}
	
	synchronized void depart() throws InterruptedException {
		while (processInBarrier != numberOfProcesses) {
			wait();
		}
		System.out.println("Barrier Opening. Departing Process Running");
		System.exit(0);
	}
	
	public static void main(String[] args) {
		Barrier barrier = new Barrier();
		
		for (int i = 1; i <= numberOfProcesses; i++) {
			DepartingProcess depart = new DepartingProcess(i, barrier);
			depart.begin();
		}
		
		for (int i = 1; i <= numberOfProcesses ; i++) {
			ArrivingProcess arrive = new ArrivingProcess(i, barrier);
			arrive.begin();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
