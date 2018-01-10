package org.example.temperature.Controller;

import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.temperature.Thermometer;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.temperature.Cooler;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TemperatureControllerImpl implements DeviceListener, PeriodicRunnable {

	/** Field for heaters dependency */
	private Heater[] heaters;
	/** Field for thermometers dependency */
	private Thermometer[] thermometers;
	/** Field for coolers dependency */
	private Cooler[] coolers;

	
	/** Temperature unit used : Kelvin */
	/** 
	 * Temperature goal for each room : 25°C
	 */
	private double temperatureGoal = 298.15d;
	
	
	
	
	
	/** Bind Method for thermometers dependency */
	public void bindThermometer(Thermometer thermometer, Map properties) {
		thermometer.addListener(this);
		System.out.println("bind thermometer " + thermometer.getSerialNumber());
	}

	/** Unbind Method for thermometers dependency */
	public void unbindThermometer(Thermometer thermometer, Map properties) {
		thermometer.removeListener(this);
		System.out.println("unbind thermometer " + thermometer.getSerialNumber());
	}

	/** Bind Method for coolers dependency */
	public void bindCooler(Cooler cooler, Map properties) {
		cooler.addListener(this);
		System.out.println("bind cooler " + cooler.getSerialNumber());
	}

	/** Unbind Method for coolers dependency */
	public void unbindCooler(Cooler cooler, Map properties) {
		cooler.removeListener(this);
		System.out.println("unbind cooler " + cooler.getSerialNumber());
	}

	/** Bind Method for heaters dependency */
	public void bindHeater(Heater heater, Map properties) {
		heater.addListener(this);
		System.out.println("bind heater " + heater.getSerialNumber());
	}

	/** Unbind Method for heaters dependency */
	public void unbindHeater(Heater heater, Map properties) {
		heater.removeListener(this);
		System.out.println("unbind heater " + heater.getSerialNumber());
	}

	/** Component Lifecycle Method */
	public void stop() {
		for (Thermometer thermometer : thermometers) {
			thermometer.removeListener(this);
		}
		for (Cooler cooler : coolers) {
			cooler.removeListener(this);
		}
		for (Heater heater : heaters) {
			heater.removeListener(this);
		}
		System.out.println("Component is stopping...");
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("Component is starting...");
	}

	
	@Override
	public void deviceAdded(GenericDevice arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deviceEvent(GenericDevice arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void devicePropertyAdded(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void devicePropertyModified(GenericDevice arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deviceRemoved(GenericDevice arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		//System.out.println("Runnable function");
		double globalTemperature = 0.0d;
		for (Thermometer thermometer : thermometers) {
			globalTemperature = globalTemperature + thermometer.getTemperature();
		}
		
		globalTemperature = globalTemperature/thermometers.length;
		if (globalTemperature < temperatureGoal) { 
			//The house is cooler than the temperature targeted so we need to activate the heaters
			for (Heater heater : heaters) {
				heater.setPowerLevel(0.1d);
			}
			for (Cooler cooler : coolers) {
				cooler.setPowerLevel(0.0d);
			}
			
		} else if (globalTemperature > temperatureGoal) {
			//The house is hotter than the temperature targeted so we need to activate the coolers
			for (Cooler cooler: coolers) {
				cooler.setPowerLevel(0.1d);
			}
			for (Heater heater : heaters) {
				heater.setPowerLevel(0.0d);
			}
		}
/*		
		for (GenericDevice light : sameLocationLigths) {
			if (changingSensor.getSensedPresence() && lightsOn < maxLightsToTurnOnPerRoom) {
				turnOn(light);
				lightsOn++;
			} else {
				turnOff(light);
			}
		}
*/
		
	}
		
	@Override
	public long getPeriod() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public TimeUnit getUnit() {
		return TimeUnit.SECONDS;
	}

}
