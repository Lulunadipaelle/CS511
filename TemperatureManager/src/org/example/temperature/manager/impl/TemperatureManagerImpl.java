package org.example.temperature.manager.impl;
import org.example.temperature.manager.administration.TemperatureManagerAdministration;
import org.example.temperature.configuration.TemperatureConfiguration;

public class TemperatureManagerImpl implements TemperatureManagerAdministration {

	/** Field for TemperatureGoal dependency */
	private TemperatureConfiguration TemperatureGoal;
	/** Injected field for the service property t_administrationService */
	private TemperatureManagerAdministration t_administrationService;

	@Override
	public void temperatureIsTooHigh(String roomName) {
		// TODO Auto-generated method stub
		System.out.println("Entering temperatureIsTooHigh in TemperatureManager");
		TemperatureGoal.setTargetedTemperature("kitchen", 298.15f);
		TemperatureGoal.getTargetedTemperature("kitchen");
		
	}

	@Override
	public void temperatureIsTooLow(String roomName) {
		// TODO Auto-generated method stub
		System.out.println("Entering temperatureIsTooLow in TemperatureManager");
		TemperatureGoal.setTargetedTemperature("kitchen", 298.15f);
		TemperatureGoal.getTargetedTemperature("kitchen");
	}

	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("Component TemperatureManager is stopping...");
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("Component TemperatureManager is starting...");
	}

}
