import java.util.Random;
import java.util.concurrent.Semaphore;

class Director implements Runnable{
	int numberOfCustomers;

  public void run(){
    while(true){
      startDirector();
    }
  }

  private void openEastAndWestDoor(){
    Museum.eastDoor = 1;
    Museum.incoming = true;
    System.out.println("Director has opened the East door!");

    Museum.westDoor = 1;
    System.out.println("Director has opened the West door!");
  }

  private void closeEastDoor(){
    Museum.eastDoor = 0;
    Museum.incoming = false;
    System.out.println("Director has closed the East door!");
  }

  private void closeWestDoor(){
    Museum.westDoor = 0;
    System.out.println("Director has closed the West door! \nDay has ended\n");
  }

  private void startDirector(){
    try{
      Thread.sleep(1000);
    }
    catch (InterruptedException e) {
        e.printStackTrace();
    }

    System.out.println("New Day!\n");
    openEastAndWestDoor();

    try{
      Thread.sleep(8000);
    }
    catch (InterruptedException e) {
        e.printStackTrace();
    }

    closeEastDoor();

    do{
      numberOfCustomers = Museum.numberOfCustomers.availablePermits();
    }
    while(numberOfCustomers != 0);

    System.out.println("Everyone left!");
    closeWestDoor();
  }
  
  public void begin(){
    (new Thread(new Director())).start();
  }
}

class Control implements Runnable{
  public static int number, status;
  public static Random randomn = new Random();
  public static int ticket, in = 0, out = 0;

  public Control(int number, int status){
    this.number = number;
    this.status = status;
  }

  private synchronized void incustomer(){
    Museum.numberOfCustomers.release();
    System.out.println("Customer #"+number+ " has walked in!");
    statusupdate();
    leave();
  }

  private synchronized void statusupdate(){
    int temp = Museum.numberOfCustomers.availablePermits();
    System.out.println("There are "+temp+ " people in the museum");
  }

  private synchronized void outcustomer(){
    try{
      Museum.numberOfCustomers.acquire();
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }
    //out++;
    System.out.println("1 Customer has left!");
    statusupdate();
  }

  public void leave(){
    int n = Museum.randomElement.nextInt(2);
    try {
      Thread.sleep(n*700);
    } 
    catch (InterruptedException e) {
      e.printStackTrace();
    }

    Control c = new Control(number, 1);
    c.begin();
  }

  public void run(){
    if (status == 0)
      incustomer();
    else if (status == 1)
      outcustomer();
  }
  
  public void begin(){
    (new Thread(new Control(this.number, this.status))).start();
  }
}

public class Museum {
  public static Semaphore numberOfCustomers; 
  public static int eastDoor, westDoor, numorders;
  public static Random randomElement = new Random();
  public static boolean incoming = false;
  
  public Museum(){
    eastDoor = 0;
    westDoor = 0;
    numberOfCustomers = new Semaphore(0);
  }
  
  public static void main(String[] args) {

    Museum museum = new Museum();

    Director director = new Director();
    director.begin();

    try {
      Thread.sleep(400);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    while(true){
      //System.out.println("Here outer with " +incoming);
      while(incoming){
        //System.out.println("Inside dodo");
        int n = randomElement.nextInt(2);
        if(n == 1){
          Control c = new Control(++numorders, 0);
          c.begin();
          //numorders++;
        }
        try {
          Thread.sleep(n*500);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      numorders=0;
      try {
      Thread.sleep(400);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
}