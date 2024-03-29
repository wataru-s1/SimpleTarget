import java.util.ArrayList;

public final class TimeUtil {

	private long startTime;
	private long restartTime;
	private long checkedTime;
	private long endTime;
	private ArrayList<Long> timeList;

	public TimeUtil() {
		startTime = System.currentTimeMillis();
		restartTime = System.currentTimeMillis();
		checkedTime = System.currentTimeMillis();
		timeList = new ArrayList<>();
	}

	public void start() {
		startTime = System.currentTimeMillis();
	}

	public void end() {
		endTime = System.currentTimeMillis();
	}

	public void stop() {
		timeList.add(System.currentTimeMillis()-restartTime);
	}

	public void restart() {
		restartTime = System.currentTimeMillis();
	}

	public void check() {
		checkedTime = System.currentTimeMillis();
	}

	public double checkMilli() {
		double t = System.currentTimeMillis()-checkedTime;
		check();
		return t;
	}

	public double checkSec() {
		double t = (System.currentTimeMillis()-checkedTime)/1000.0;
		check();
		return t;
	}

	public double getTimeMilli() {
		return endTime-startTime;
	}

	public double getTimeSec() {
		return (endTime-startTime)/1000.0;
	}

	public double getTotalTimeMilli() {
		long totalTime = 0;
		for (long t : timeList) {
			totalTime+=t;
		}
		return totalTime;
	}

	public double getTotalTimeSec() {
		return getTotalTimeMilli()/1000.0;
	}

	public ArrayList<Long> getTimeList() {
		return timeList;
	}
}
