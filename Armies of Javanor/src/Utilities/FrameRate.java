package Utilities;

public final class FrameRate {

	private static FrameAnimator animator;
	
	public static void initialize() {
		
		//Begin the animator
		animator = new FrameAnimator();
		animator.setDaemon(true);
		animator.start();
		
	}
	
	public static int frame() {
		
		return animator.frame();
		
	}
	
}
