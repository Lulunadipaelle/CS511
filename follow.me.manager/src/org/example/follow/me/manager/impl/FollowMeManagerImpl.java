package org.example.follow.me.manager.impl;

import com.example.follow.me.configuration.FollowMeConfiguration;
import java.util.Map;

import org.example.follow.me.manager.FollowMeAdministration;

public class FollowMeManagerImpl implements FollowMeAdministration {

	/** Injected field for the component property high_level_goals */
	private String high_level_goals;
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
			break;
		case MEDIUM:
			IlluminanceGoal.setMaximumNumberOfLightsToTurnOn(2);
			break;
		case FULL:
			IlluminanceGoal.setMaximumNumberOfLightsToTurnOn(3);
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
			return org.example.follow.me.manager.IlluminanceGoal.FULL;
		}
	}


}
