package Utilities;

public class FrameAnimator extends Thread {

	private int frame = 1;
	
	public void run() {
		
		while(true) {
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			frame++;
			
			if(frame > 2) {
				frame = 1;
			}
			
		}
		
	}
	
	public int frame() {
		
		return frame;
		
	}
	
}
