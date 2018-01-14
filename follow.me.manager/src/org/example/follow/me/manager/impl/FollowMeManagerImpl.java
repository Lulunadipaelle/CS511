package org.example.follow.me.manager.impl;

import com.example.follow.me.configuration.FollowMeConfiguration;

import org.example.follow.me.manager.EnergyGoal;
import org.example.follow.me.manager.FollowMeAdministration;

public class FollowMeManagerImpl implements FollowMeAdministration {

	/** Field for IlluminanceGoal dependency */
	private FollowMeConfiguration IlluminanceGoal;
	/** Injected field for the service property m_administrationService */
	private FollowMeAdministration m_administrationService;

	/** Component Lifecycle Method */
	public void stop() {
		// TODO: Add your implementation code here
		System.out.println("Component FollowMeManager is stopping...");
	}

	/** Component Lifecycle Method */
	public void start() {
		// TODO: Add your implementation code here
		System.out.println("Component FollowMeManager is starting...");
	}

	@Override
	public void setIlluminancePreference(org.example.follow.me.manager.IlluminanceGoal illuminanceGoal) {
		switch (illuminanceGoal) {
		case SOFT:
			IlluminanceGoal.setMaximumNumberOfLightsToTurnOn(1);
			IlluminanceGoal.setMaximumAllowedEnergyInRoom(500d);
			break;
		case MEDIUM:
			IlluminanceGoal.setMaximumNumberOfLightsToTurnOn(2);
			IlluminanceGoal.setMaximumAllowedEnergyInRoom(2750d);
			break;
		case FULL:
			IlluminanceGoal.setMaximumNumberOfLightsToTurnOn(3);
			IlluminanceGoal.setMaximumAllowedEnergyInRoom(4000d);
			break;
		}

	}

	@Override
	public org.example.follow.me.manager.IlluminanceGoal getIlluminancePreference() {
		int MaxLights = IlluminanceGoal.getMaximumNumberOfLightsToTurnOn();
		System.out.println(MaxLights);
		switch (MaxLights) {
		case 1:
			return org.example.follow.me.manager.IlluminanceGoal.SOFT;
		case 2:
			return org.example.follow.me.manager.IlluminanceGoal.MEDIUM;
		case 3:
			return org.example.follow.me.manager.IlluminanceGoal.FULL;
		default:
			System.out.println("Nombre de lampes à allumer invalide");
			System.out.println("set FULL by default");
			return org.example.follow.me.manager.IlluminanceGoal.FULL;
		}
	}

	@Override
	public void setEnergyGoal(EnergyGoal energyGoal) {
		if (energyGoal.equals(EnergyGoal.HIGH)) {
			System.out.println("CHANGING TO HIGH");
			IlluminanceGoal.setMaximumAllowedEnergyInRoom(1000d);
		} else if (energyGoal.equals(EnergyGoal.MEDIUM)) {
			System.out.println("CHANGING TO MEDIUM");
			IlluminanceGoal.setMaximumAllowedEnergyInRoom(250d);
		} else if (energyGoal.equals(EnergyGoal.LOW)) {
			System.out.println("CHANGING TO LOW");
			IlluminanceGoal.setMaximumAllowedEnergyInRoom(100d);
		}
		return;

	}

	@Override
	public EnergyGoal getEnergyGoal() {
		if (IlluminanceGoal.getMaximumAllowedEnergyInRoom() == 1000d) {
			return (EnergyGoal.HIGH);
		} else if (IlluminanceGoal.getMaximumAllowedEnergyInRoom() == 250d) {
			return (EnergyGoal.MEDIUM);
		} else if (IlluminanceGoal.getMaximumAllowedEnergyInRoom() == 100d) {
			return (EnergyGoal.LOW);
		} else {
			return null;
		}
	}

}
