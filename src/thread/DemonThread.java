package thread;

public class DemonThread implements Runnable{
	Thread t;
	EnsembleThread enthread;
	
	
	public DemonThread(Thread t, EnsembleThread enthread) {
		super();
		this.t = t;
		this.enthread = enthread;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!enthread.stop){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		t.stop();
	}

}
