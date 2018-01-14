package com.example.follow.me.configuration;
 
/**
 * The FollowMeConfiguration service allows to configure the Follow Me
 * application.
 */
public interface FollowMeConfiguration {
    public int getMaximumNumberOfLightsToTurnOn();
    public void setMaximumNumberOfLightsToTurnOn(int maximumNumberOfLightsToTurnOn);
 
    /**
     * Gets the maximum allowed energy consumption in Watts in each room
     * 
     * @return the maximum allowed energy consumption in Watts/hours
     */
    public double getMaximumAllowedEnergyInRoom();
 
    /**
     * Sets the maximum allowed energy consumption in Watts in each room
     * 
     * @param maximumEnergy
     *            the maximum allowed energy consumption in Watts/hours in each room
     */
    public void setMaximumAllowedEnergyInRoom(double maximumEnergy);

	public double getTargetedIlluminance();

	/**
	 * Sets the targeted illuminance for each room
	 * 
	 * @param illuminance
	 *            the targeted illuminance in lumens for each room
	 */
	public void setTargetedIlluminance(double illuminance);
}