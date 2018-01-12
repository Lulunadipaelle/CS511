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
		//We need to lower the room temperature by 1 Kelvin
		System.out.println("Rising temperature in "+roomName);
		TemperatureGoal.setTargetedTemperature(roomName, TemperatureGoal.getTargetedTemperature(roomName)-1);

		
	}

	@Override
	public void temperatureIsTooLow(String roomName) {
		//We need to rise the room temperature by 1 Kelvin
		TemperatureGoal.setTargetedTemperature(roomName, TemperatureGoal.getTargetedTemperature(roomName)+1);
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