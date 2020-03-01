package Utilities;

public class Watcher extends Thread { //FIXME WARNING: THIS CLASS IS NOT USED ANYMORE, CONSIDER REMOVING

	private WatchCondition condition;
	
	public Watcher(WatchCondition condition) {
		
		this.condition = condition;
		
	}
	
	public void run() {
		
		while(true) {
			
			condition.watch();
			
		}
		
	}
	
}
