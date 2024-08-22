package com.xquadro.tabataTimer;


public class TabataModel2 {
	public static final int PREPARE = 1;
	public static final int EFFORD = 2;
	public static final int REST = 3;
	public static final int END = 4;
	public static final int NO_SOUND = 0;
	public static final int EFFORD_SOUND = 1;
	public static final int REST_SOUND = 2;
	
	
	private int restTime = 0;
	private int roundsCount = 0;
	
	private int roundTime = 0;
	private int timeLeft = 0;
	private int totalTime = 0;
	
	private int roundsLeft = 0;
	private int activeRoundTime = 0;
	private int activeIntervalType = 0;
	private int nextIntervalType = 0;
	private int activeIntervalTime = 0;
	private boolean blips = false;
	
	
	
	public TabataModel2 (int prepareTime, int effordTime, int restTime, int roundsCount, boolean blips) {
		super();
		this.restTime = restTime;
		this.roundsCount = roundsCount;
		roundTime = effordTime + restTime;
		totalTime = prepareTime + roundTime * roundsCount - restTime;
		timeLeft = totalTime;
		
		roundsLeft = roundsCount;
		activeIntervalType = PREPARE;
		this.blips = blips;
	}
	
	public void onTick(){
		if(timeLeft == 0) {
			activeIntervalTime = 0;
			activeIntervalType = END;
			nextIntervalType = 0;
			return;
		}
				
		timeLeft--;
			
		int prepareLeft = timeLeft - (roundTime * roundsCount - restTime);
		
		if (prepareLeft >= 0){
			activeIntervalTime = prepareLeft;
			activeIntervalType = PREPARE;
			nextIntervalType = EFFORD;
		} else {	
			roundsLeft = (timeLeft + restTime) / roundTime;
			activeRoundTime = (timeLeft + restTime) % roundTime;

			activeIntervalTime = ( activeRoundTime - restTime >= 0)?(activeRoundTime - restTime):(activeRoundTime);
			activeIntervalType = (activeRoundTime - restTime >= 0)?EFFORD:REST;
			nextIntervalType = (activeIntervalType == REST)?EFFORD:REST;
		}

	}

	public int getRounds() {
		return roundsCount;
	}

	public int getRoundsLeft() {
		return roundsLeft;
	}
	
	public int getCurrentRound() {
		return roundsCount - roundsLeft;
	}

	public int getActiveIntervalTime() {
		return activeIntervalTime;
	}

	public int getActiveIntervalType() {
		return activeIntervalType;
	}
	
	public int getTotalTime(){
		return totalTime;
	}
	
	public int getTimeLeft(){
		return timeLeft;
	}
	
	public int hasSound(){
		if (nextIntervalType == EFFORD && activeIntervalTime < 3)
			return EFFORD_SOUND;
		if (nextIntervalType == REST && activeIntervalTime == 1 && blips)
			return EFFORD_SOUND;
		if (nextIntervalType == REST && activeIntervalTime < 1)
			return REST_SOUND;
		return NO_SOUND;
	}
	
}
