package org.example.follow.me.manager;
 
/**
 * This enum describes the different illuminance goals associated with the
 * manager.
 */
public enum IlluminanceGoal {
 
    /** The goal associated with soft illuminance. */
    SOFT(1, 500d),
    /** The goal associated with medium illuminance. */
    MEDIUM(2, 2750d),
    /** The goal associated with full illuminance. */
    FULL(3, 4000d);
 
    /** The number of lights to turn on. */
    private int numberOfLightsToTurnOn;
    private double illuminance;
 
    /**
     * Gets the number of lights to turn On.
     * 
     * @return the number of lights to turn On.
     */
    public int getNumberOfLightsToTurnOn() {
        return numberOfLightsToTurnOn;
    }
 
	public double getIlluminance() {
		return illuminance;
	}


	private IlluminanceGoal(int numberOfLightsToTurnOn, double illuminance) {
		this.numberOfLightsToTurnOn = numberOfLightsToTurnOn;
		this.illuminance = illuminance;
	}

}