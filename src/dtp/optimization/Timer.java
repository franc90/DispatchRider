package dtp.optimization;

public class Timer{
	public static long time=0;
	public  long start;
	public  long stop;


	
	public void startTimer() {
		start = System.nanoTime();
	}

	public void stopTimer() {
		stop=System.nanoTime()-start;
		time+=stop;
		System.out.println("Iteration: " + stop + " Total: " + time);
	}
	
}