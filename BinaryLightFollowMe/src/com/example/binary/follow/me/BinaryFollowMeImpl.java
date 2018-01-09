package com.example.binary.follow.me;

import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.follow.me.configuration.FollowMeConfiguration;
import fr.liglab.adele.icasa.device.light.Photometer;

public class BinaryFollowMeImpl implements DeviceListener, FollowMeConfiguration {

	/** Field for presenceSensors dependency */
	private PresenceSensor[] presenceSensors;
	/** Field for binaryLights dependency */
	private BinaryLight[] binaryLights;
	/** Field for dimmerLights dependency */
	private DimmerLight[] dimmerLights;
	/** Field for photometers dependency */
	private Photometer[] photometers;
	/** 
	* The maximum number of lights to turn on when a user enters the room :
	**/
	private int maxLightsToTurnOnPerRoom = 3;

	/**
	* The maximum energy consumption allowed in a room in Watt:
	**/
	private double maximumEnergyConsumptionAllowedInARoom = 100.0d;

    /**
    * The targeted illuminance in each room
    **/
    private double targetedIlluminance = 4000.0d;
	
	
	/** Bind Method for presenceSensors dependency */
	public synchronized void bindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
		presenceSensor.addListener(this);
		System.out.println("bind presence sensor " + presenceSensor.getSerialNumber());
	}

	/** Unbind Method for presenceSensors dependency */
	public synchronized void unbindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
		presenceSensor.removeListener(this);
		System.out.println("Unbind presence sensor " + presenceSensor.getSerialNumber());
	}

	/** Bind Method for binaryLights dependency */
	public void bindBinaryLight(BinaryLight binaryLight, Map properties) {
		binaryLight.addListener(this);
		System.out.println("bind binary light " + binaryLight.getSerialNumber());
	}

	/** Unbind Method for binaryLights dependency */
	public void unbindBinaryLight(BinaryLight binaryLight, Map properties) {
		binaryLight.removeListener(this);
		System.out.println("unbind binary light " + binaryLight.getSerialNumber());
	}

	/** Bind Method for dimmerLights dependency */
	public void bindDimmerLight(DimmerLight dimmerLight, Map properties) {
		dimmerLight.addListener(this);
		System.out.println("bind dimmer light " + dimmerLight.getSerialNumber());
	}

	/** Unbind Method for dimmerLights dependency */
	public void unbindDimmerLight(DimmerLight dimmerLight, Map properties) {
		dimmerLight.removeListener(this);
		System.out.println("unbind dimmer light " + dimmerLight.getSerialNumber());
	}
	
	/** Bind Method for photometer dependency */
	public void bindPhotometer(Photometer photometer, Map properties) {
		photometer.addListener(this);
		System.out.println("bind photometer " + photometer.getSerialNumber());
	}

	/** Unbind Method for photometer dependency */
	public void unbindPhotometer(Photometer photometer, Map properties) {
		photometer.addListener(this);
		System.out.println("unbind photometer " + photometer.getSerialNumber());
	}
	
	
	
	
	
	/** Component Lifecycle Method */
	public synchronized void stop() {
		for (PresenceSensor sensor : presenceSensors) {
			sensor.removeListener(this);
		}
		for (BinaryLight light : binaryLights) {
			light.removeListener(this);
		}
		for (DimmerLight light : dimmerLights) {
			light.removeListener(this);
		}
		for (Photometer photo : photometers) {
			photo.removeListener(this);
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

	/**
	 * The name of the LOCATION property
	 */
	public static final String LOCATION_PROPERTY_NAME = "Location";

	/**
	 * The name of the location for unknown value
	 */
	public static final String LOCATION_UNKNOWN = "unknown";

	
	
	
	@Override
	public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {

		int lightsOn = 0;
		double illuminance = 0;
		if (device instanceof PresenceSensor) {
			assert device instanceof PresenceSensor : "device must be a presence sensors only";

			//based on that assumption we can cast the generic device without checking via instanceof
			PresenceSensor changingSensor = (PresenceSensor) device;
			
			// check the change is related to presence sensing
			if (propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {
				// get the location where the sensor is:
				String detectorLocation = (String) changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
				// if the location is known :
				if (!detectorLocation.equals(LOCATION_UNKNOWN)) {
					// get the related binary lights
					List<GenericDevice> sameLocationLigths = getLightFromLocation(detectorLocation);
					
					for (GenericDevice light : sameLocationLigths) {
						
						if (changingSensor.getSensedPresence() && lightsOn < maxLightsToTurnOnPerRoom) {
							turnOn(light);
							lightsOn++;
						} else {
							turnOff(light);
						}
					}
					
					for (GenericDevice light : sameLocationLigths) {
						if (changingSensor.getSensedPresence() && lightsOn < maxLightsToTurnOnPerRoom) {
							turnOn(light);
							lightsOn++;
						} else {
							turnOff(light);
						}
					}

					lightsOn = 0;
				}
			}

			// check the change is related to presence sensing
			if (propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {
				// get the location of the changing sensor:
				String detectorLocation = (String) changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
				System.out.println(
						"The device with the serial number" + changingSensor.getSerialNumber() + " has changed");
				System.out.println("This sensor is in the room :" + detectorLocation);
			}
		}

		if (device instanceof BinaryLight) {
			BinaryLight changingLight = (BinaryLight) device;
			if (propertyName.equals(BinaryLight.LOCATION_PROPERTY_NAME)) {
				// get the location where the sensor is:
				String lightLocation = (String) changingLight.getPropertyValue(LOCATION_PROPERTY_NAME);
				// if the location is known :
				if (!lightLocation.equals(LOCATION_UNKNOWN)) {
					// get the related Presence Sensors
					List<PresenceSensor> sameLocationPresenceSensors = getPresenceSensorFromLocation(lightLocation);
					List<GenericDevice> lightsInTheRoom = getLightFromLocation(lightLocation);
					int lightOn = 0;

					for (GenericDevice dev : lightsInTheRoom) {
						System.out.println(dev);
					}

					for (GenericDevice light : lightsInTheRoom) {
						if (isOn(light)) {
							lightOn++;
						}
					}

					for (PresenceSensor presenceSensor : sameLocationPresenceSensors) {
						// and switch the light on/off depending on the sensed presence in the room
						if (presenceSensor.getSensedPresence() && lightOn < maxLightsToTurnOnPerRoom) {
							changingLight.turnOn();
						} else {
							changingLight.turnOff();
						}
					}

				} else {
					changingLight.turnOff();
				}

			}
			int oldLightsOn = 0;

			List<PresenceSensor> oldSamePresenceSensors = getPresenceSensorFromLocation(oldValue.toString());
			List<GenericDevice> oldSameLights = getLightFromLocation(oldValue.toString());
			System.out.println(oldValue.toString());
			for (GenericDevice light : oldSameLights) {
				if (isOn(light)) {
					oldLightsOn++;
				}
			}
			System.out.println("nb de lumières encore allumées dans l'ancienne pièce : " + oldLightsOn);
			for (PresenceSensor presSensor : oldSamePresenceSensors) {
				for (GenericDevice light : oldSameLights) {
					if (presSensor.getSensedPresence() && oldLightsOn < maxLightsToTurnOnPerRoom && !isOn(light)) {
						turnOn(light);
						oldLightsOn++;
					} else if (oldLightsOn > maxLightsToTurnOnPerRoom) {
						turnOff(light);
					}

				}
			}
		}

		if (device instanceof DimmerLight) {
			DimmerLight changingLight = (DimmerLight) device;
			if (propertyName.equals(BinaryLight.LOCATION_PROPERTY_NAME)) {
				// get the location where the sensor is:
				String lightLocation = (String) changingLight.getPropertyValue(LOCATION_PROPERTY_NAME);
				// if the location is known :
				if (!lightLocation.equals(LOCATION_UNKNOWN)) {
					// get the related Presence Sensors
					List<PresenceSensor> sameLocationPresenceSensors = getPresenceSensorFromLocation(lightLocation);
					List<GenericDevice> lightsInTheRoom = getLightFromLocation(lightLocation);

					for (GenericDevice dev : lightsInTheRoom) {
						System.out.println(dev);
					}

					int lightOn = 0;

					for (GenericDevice light : lightsInTheRoom) {
						if (isOn(light)) {
							lightOn++;
						}
					}

					for (PresenceSensor presenceSensor : sameLocationPresenceSensors) {
						// and switch the light on/off depending on the sensed presence in the room
						if (presenceSensor.getSensedPresence() && lightOn < maxLightsToTurnOnPerRoom) {
							changingLight.setPowerLevel(1);
						} else {
							changingLight.setPowerLevel(0);
						}
					}

				} else {
					changingLight.setPowerLevel(0);
				}
			}

		}
		int oldLightsOn = 0;

		List<PresenceSensor> oldSamePresenceSensors = getPresenceSensorFromLocation(oldValue.toString());
		List<GenericDevice> oldSameLights = getLightFromLocation(oldValue.toString());
		System.out.println(oldValue.toString());

		for (GenericDevice light : oldSameLights) {
			if (isOn(light)) {
				oldLightsOn++;
			}
		}
		for (PresenceSensor presSensor : oldSamePresenceSensors) {
			for (GenericDevice light : oldSameLights) {
				if (presSensor.getSensedPresence() && oldLightsOn < maxLightsToTurnOnPerRoom && !isOn(light)) {
					turnOn(light);
					oldLightsOn++;
				} else if (oldLightsOn > maxLightsToTurnOnPerRoom) {
					turnOff(light);
				}
			}
		}

	}

	@Override
	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deviceRemoved(GenericDevice arg0) {
		// TODO Auto-generated method stub

	}

	//			      for(DimmerLight dlgiht : dLightsInTheRoom){
	//			    	  if(dlgiht.getPowerLevel() == 1){
	//			    		  lightOn++;
	//			    	  }
	//			      }
	/**
	 * Return all BinaryLight from the given location
	 * 
	 * @param location
	 *            : the given location
	 * @return the list of matching BinaryLights
	 */

	private synchronized List<PresenceSensor> getPresenceSensorFromLocation(String location) {
		List<PresenceSensor> presenceSensorsLocation = new ArrayList<PresenceSensor>();
		for (PresenceSensor presSens : presenceSensors) {
			if (presSens.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				presenceSensorsLocation.add(presSens);
			}
		}
		return presenceSensorsLocation;
	}

	private synchronized List<GenericDevice> getLightFromLocation(String location) {
		List<GenericDevice> lightLocation = new ArrayList<GenericDevice>();
		for (DimmerLight dimLi : dimmerLights) {
			if (dimLi.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				lightLocation.add(dimLi);
			}
		}
		for (BinaryLight binLight : binaryLights) {
			if (binLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				lightLocation.add(binLight);
			}
		}
		return lightLocation;
	}

	private synchronized List<GenericDevice> getPhotometerFromLocation(String location) {
		List<GenericDevice> photometerLocation = new ArrayList<GenericDevice>();
		for (Photometer photo : photometers) {
			if (photo.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				photometerLocation.add(photo);
			}
		}
		return photometerLocation;
	}
	
	
	private synchronized void turnOn(GenericDevice light) {
		if (light instanceof BinaryLight) {
			((BinaryLight) light).turnOn();
		}
		if (light instanceof DimmerLight) {
			((DimmerLight) light).setPowerLevel(1);
		}
	}

	private synchronized void turnOff(GenericDevice light) {
		if (light instanceof BinaryLight) {
			((BinaryLight) light).turnOff();
		}
		if (light instanceof DimmerLight) {
			((DimmerLight) light).setPowerLevel(0);
		}
	}

	private synchronized boolean isOn(GenericDevice light) {
		boolean res = false;
		if (light instanceof BinaryLight) {
			if (((BinaryLight) light).getPowerStatus()) {
				res = true;
			}
		}
		if (light instanceof DimmerLight) {
			if (((DimmerLight) light).getPowerLevel() == 1) {
				res = true;
			}
		}
		return res;
	}

	@Override
	public int getMaximumNumberOfLightsToTurnOn() {
		return maxLightsToTurnOnPerRoom;
	}

	@Override
	public void setMaximumNumberOfLightsToTurnOn(int maximumNumberOfLightsToTurnOn) {
		maxLightsToTurnOnPerRoom = maximumNumberOfLightsToTurnOn;

	}

	@Override
	public double getMaximumAllowedEnergyInRoom() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaximumAllowedEnergyInRoom(double maximumEnergy) {
		// TODO Auto-generated method stub

	}


}
