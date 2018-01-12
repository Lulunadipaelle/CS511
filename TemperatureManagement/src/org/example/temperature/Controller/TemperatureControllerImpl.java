package org.example.temperature.Controller;

import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.temperature.Thermometer;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.Photometer;
import fr.liglab.adele.icasa.device.temperature.Cooler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.example.temperature.configuration.TemperatureConfiguration;

public class TemperatureControllerImpl implements DeviceListener, PeriodicRunnable, TemperatureConfiguration {

	/** Field for heaters dependency */
	private Heater[] heaters;
	/** Field for thermometers dependency */
	private Thermometer[] thermometers;
	/** Field for coolers dependency */
	private Cooler[] coolers;

	
	/** Temperature unit used : Kelvin */
	/** 
	 * Temperature goal for al the flat : 25°C
	 * Temperature goal for rooms
	 */
	private double temperatureGoal = 298.15d;
	private double temperatureGoalKitchen = 288.15d;
	private double temperatureGoalLivingroom = 291.15d;
	private double temperatureGoalBedroom = 293.15d;
	private double temperatureGoalBathroom = 296.15d;
	
	private double Kp = 0.02d;
	//private double error = 0.0d;
	/**
	 * The name of the LOCATION property
	 */
	public static final String LOCATION_PROPERTY_NAME = "Location";

	/**
	 * The name of the location for unknown value
	 */
	public static final String LOCATION_UNKNOWN = "unknown";

	
	
	
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
		/**
		 * Choose between setting the global temperature or the temperature for each room
		 */
		//System.out.println("Scheduled task : setGlobalTemperature("+ temperatureGoal +")");
		//setGlobalTemperature();
		
		
		System.out.println("Scheduled task : setTemperatureForRoom(bathroom,"+ temperatureGoalBathroom + ")");
		setTemperatureForRoom("bathroom");

		System.out.println("Scheduled task : setTemperatureForRoom(bedroom,"+ temperatureGoalBedroom + ")");
		setTemperatureForRoom("bedroom");
		
		System.out.println("Scheduled task : setTemperatureForRoom(livingroom,"+ temperatureGoalLivingroom + ")");
		setTemperatureForRoom("livingroom");
		
		System.out.println("Scheduled task : setTemperatureForRoom(kitchen,"+ temperatureGoalKitchen + ")");
		setTemperatureForRoom("kitchen");
	}
	
	private synchronized void setGlobalTemperature() {
		double globalTemperature = 0.0d;
		for (Thermometer thermometer : thermometers) {
			globalTemperature = globalTemperature + thermometer.getTemperature();
		}
		globalTemperature = globalTemperature/thermometers.length;
		if (globalTemperature < temperatureGoal) { 
			//The house is cooler than the temperature targeted so we need to activate the heaters
			for (Heater heater : heaters) {
				heater.setPowerLevel(0.01d);
			}
			for (Cooler cooler : coolers) {
				cooler.setPowerLevel(0.0d);
			}
			
		} else if (globalTemperature > temperatureGoal) {
			//The house is hotter than the temperature targeted so we need to activate the coolers
			for (Cooler cooler: coolers) {
				cooler.setPowerLevel(0.01d);
			}
			for (Heater heater : heaters) {
				heater.setPowerLevel(0.0d);
			}
		}
	}
	
	private synchronized void setTemperatureForRoom(String location) {
		double roomTemperature = 0.0d;
		double targetedRoomTemperature = 0.0d;
		switch(location) {
		case "bathroom":
			targetedRoomTemperature = temperatureGoalBathroom;
			break;
		case "livingroom":
			targetedRoomTemperature = temperatureGoalLivingroom;
			break;
		case "bedroom":
			targetedRoomTemperature = temperatureGoalBedroom;
			break;
		case "kitchen":
			targetedRoomTemperature = temperatureGoalKitchen;
			break;
		}
		List<Thermometer> sameLocationThermometers = getThermometerFromLocation(location);
		List<Heater> sameLocationHeaters = getHeaterFromLocation(location);
		List<Cooler> sameLocationCoolers = getCoolerFromLocation(location);
		for (Thermometer thermometer : sameLocationThermometers) {
			roomTemperature = roomTemperature + thermometer.getTemperature();
		}
		roomTemperature = roomTemperature/sameLocationThermometers.size();

		/*erreur = consigne - mesure;
    commande = Kp * erreur ;*/
		
		double error = targetedRoomTemperature - roomTemperature;
		double commande = Kp * error;
		System.out.println(commande);
		if ((commande < -1.0d) || (commande > 1.0d)) {
			if (commande < 0.0d) {
				for (Cooler cooler: sameLocationCoolers) {
					cooler.setPowerLevel(1.0d);
				}
				for (Heater heater : sameLocationHeaters) {
					heater.setPowerLevel(0.0d);
				}
			} else if (commande > 0.0d) {
				for (Heater heater : sameLocationHeaters) {
					heater.setPowerLevel(1.0d);
				}
				for (Cooler cooler : sameLocationCoolers) {
					cooler.setPowerLevel(0.0d);
				}
			}
		} else {
			if (commande < 0.0d) {
				for (Cooler cooler: sameLocationCoolers) {
					cooler.setPowerLevel(-commande);
				}
				for (Heater heater : sameLocationHeaters) {
					heater.setPowerLevel(0.0d);
				}
			} else if (commande >0.0d) {
				for (Heater heater : sameLocationHeaters) {
					heater.setPowerLevel(commande);
				}
				for (Cooler cooler : sameLocationCoolers) {
					cooler.setPowerLevel(0.0d);
				}
			}
		}
		/*
		if (roomTemperature < targetedRoomTemperature) { 
			//The room is cooler than the temperature targeted so we need to activate the heaters
			for (Heater heater : sameLocationHeaters) {
				heater.setPowerLevel(0.01d);
			}
			for (Cooler cooler : sameLocationCoolers) {
				cooler.setPowerLevel(0.0d);
			}
			
		} else if (roomTemperature > targetedRoomTemperature) {
			//The room is hotter than the temperature targeted so we need to activate the coolers
			for (Cooler cooler: sameLocationCoolers) {
				cooler.setPowerLevel(0.01d);
			}
			for (Heater heater : sameLocationHeaters) {
				heater.setPowerLevel(0.0d);
			} */
		 if ((int) roomTemperature == (int) targetedRoomTemperature) {
			//The room is nice so we turn off the devices
			for (Cooler cooler: sameLocationCoolers) {
				cooler.setPowerLevel(0.0d);
			}
			for (Heater heater : sameLocationHeaters) {
				heater.setPowerLevel(0.0d);
			}
		}
	}
	
	
	private synchronized List<Thermometer> getThermometerFromLocation(String location) {
		List<Thermometer> thermometerLocation = new ArrayList<Thermometer>();
		for (Thermometer thermometer : thermometers) {
			if (thermometer.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				thermometerLocation.add(thermometer);
			}
		}
		return thermometerLocation;
	}
	
	
	private synchronized List<Heater> getHeaterFromLocation(String location) {
		List<Heater> heaterLocation = new ArrayList<Heater>();
		for (Heater heater : heaters) {
			if (heater.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				heaterLocation.add(heater);
			}
		}
		return heaterLocation;
	}
	
	private synchronized List<Cooler> getCoolerFromLocation(String location) {
		List<Cooler> coolerLocation = new ArrayList<Cooler>();
		for (Cooler cooler : coolers) {
			if (cooler.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				coolerLocation.add(cooler);
			}
		}
		return coolerLocation;
	}
	
	/**
	 * Set the period of the periodic run() function (modify the return value) in milliseconds
	 */
	@Override
	public long getPeriod() {
		return 1000;
	}

	@Override
	public TimeUnit getUnit() {
		return TimeUnit.SECONDS;
	}

	@Override
	public void setTargetedTemperature(String targetedRoom, float temperature) {
		switch(targetedRoom) {
		
		case "bedroom":
			temperatureGoalBedroom = (double) temperature;
			break;
		case "bathroom":
			temperatureGoalBathroom = (double) temperature;
			break;
		case "livingroom":
			temperatureGoalLivingroom = (double) temperature;
			break;
		case "kitchen":
			temperatureGoalKitchen = (double) temperature;
			break;
		default:
			System.out.println("ERROR : Room name not recognized, setting global temperature targeted (setTargetedTemperature)");
			temperatureGoal = (double) temperature; 
			break;
		}
		
	}

	@Override
	public float getTargetedTemperature(String room) {
		switch(room) {
		case "bedroom":
			return (float) temperatureGoalBedroom;
		case "bathroom":
			return (float) temperatureGoalBathroom;
		case "livingroom":
			return (float) temperatureGoalLivingroom;
		case "kitchen":
			return (float) temperatureGoalKitchen;
		default:
			System.out.println("ERROR : Room name not recognized, displaying global temperature targeted (getTargetedTemperature)");
			return (float) temperatureGoal; 
		}

	}

}
