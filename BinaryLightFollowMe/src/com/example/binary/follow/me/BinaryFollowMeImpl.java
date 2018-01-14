package com.example.binary.follow.me;

import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.service.zone.size.calculator.ZoneSizeCalculator;
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
	/** Field for zoneSizeCalculator dependency */
	private ZoneSizeCalculator zoneSizeCalculator;

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

	public final static double ONE_WATT_TO_ONE_LUMEN = 680.0d;

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
		int oldLightsOn = 0;
		double illuminance = 0;
		double maxlight = 0;
		double oldmaxlight = 0;

		if (maxLightsToTurnOnPerRoom * 100d > maximumEnergyConsumptionAllowedInARoom) {
			maxlight = maximumEnergyConsumptionAllowedInARoom / 100d;
		} else {
			maxlight = maxLightsToTurnOnPerRoom;
		}
		oldmaxlight = maxlight;

		if (device instanceof PresenceSensor) {
			assert device instanceof PresenceSensor : "device must be a presence sensors only";

			//based on that assumption we can cast the generic device without checking via instanceof
			PresenceSensor changingSensor = (PresenceSensor) device;

			// check the change is related to presence sensing
			if (propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {
				// get the location where the sensor is:
				String detectorLocation = (String) changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME);

				if (dimmerConfig(changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME).toString()) < maxlight)
					maxlight = dimmerConfig(changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME).toString());

				// if the location is known :
				if (!detectorLocation.equals(LOCATION_UNKNOWN)) {
					// get the related binary lights
					List<BinaryLight> sameLocationBLights = getBLightFromLocation(detectorLocation);
					List<DimmerLight> sameLocationDLights = getDLightFromLocation(detectorLocation);
					//					List<GenericDevice> sameLocationLigths = getLightFromLocation(detectorLocation);

					for (BinaryLight binaryLight : sameLocationBLights) {
						if (changingSensor.getSensedPresence() && lightsOn < Math.floor(maxlight)) {
							turnOn(binaryLight);
							;
							lightsOn++;
						} else {
							binaryLight.turnOff();
						}
					}

					for (DimmerLight dimmerLight : sameLocationDLights) {
						if (changingSensor.getSensedPresence() && dimmerLight.getPowerLevel() == 0.0
								&& maxlight - lightsOn >= 1) {
							turnOn(dimmerLight);
							lightsOn++;
						} else if (changingSensor.getSensedPresence() && maxlight - lightsOn < 1
								&& maxlight - lightsOn > 0) {
							dimmerLight.setPowerLevel((maxlight - lightsOn));
							lightsOn = (int) maxlight;
						} else {
							turnOff(dimmerLight);
							;
						}
					}
				}
			}
			//
			//			// check the change is related to presence sensing
			//			if (propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {
			//				// get the location of the changing sensor:
			//				String detectorLocation = (String) changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
			//				System.out.println(
			//						"The device with the serial number" + changingSensor.getSerialNumber() + " has changed");
			//				System.out.println("This sensor is in the room :" + detectorLocation);
			//			}
			//		}

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
						List<BinaryLight> sameLocationBLights = getBLightFromLocation(lightLocation);
						List<DimmerLight> sameLocationDLights = getDLightFromLocation(lightLocation);

						if (dimmerConfig(lightLocation) < maxlight)
							maxlight = dimmerConfig(lightLocation);

						for (GenericDevice light : lightsInTheRoom) {
							turnOff(light);
						}

						for (PresenceSensor sens : sameLocationPresenceSensors) {

							for (BinaryLight binaryLight : sameLocationBLights) {
								if (sens.getSensedPresence() && lightsOn < Math.floor(maxlight)) {
									turnOn(binaryLight);
									lightsOn++;
								} else {
									turnOff(binaryLight);
								}
							}
							for (DimmerLight dimmerLight : sameLocationDLights) {
								if (sens.getSensedPresence() && dimmerLight.getPowerLevel() == 0.0
										&& maxlight - lightsOn >= 1) {
									turnOn(dimmerLight);
									lightsOn++;
								} else if (sens.getSensedPresence() && maxlight - lightsOn < 1
										&& maxlight - lightsOn > 0) {
									dimmerLight.setPowerLevel((maxlight - lightsOn));
									lightsOn = (int) maxlight;
								} else {
									turnOff(dimmerLight);
								}
							}
						}

						List<BinaryLight> oldBLights = getBLightFromLocation(oldValue.toString());
						List<PresenceSensor> oldPresSens = getPresenceSensorFromLocation(oldValue.toString());
						List<DimmerLight> oldDLights = getDLightFromLocation(oldValue.toString());
						List<GenericDevice> oldLights = getLightFromLocation(oldValue.toString());

						if (dimmerConfig(oldValue.toString()) < oldmaxlight)
							maxlight = dimmerConfig(oldValue.toString());
						else
							maxlight = oldmaxlight;

						for (PresenceSensor sens : oldPresSens) {
							for (GenericDevice light : oldLights) {
								if (isOn(light)) {
									turnOff(light);
								}

								for (BinaryLight binaryLight : oldBLights) {
									if (sens.getSensedPresence() && oldLightsOn < Math.floor(maxlight)) {
										if (!isOn(binaryLight)) {
											turnOn(binaryLight);
											;
											oldLightsOn++;
										}
									}
								}
								for (DimmerLight dimmerLight : oldDLights) {

									if (sens.getSensedPresence() && dimmerLight.getPowerLevel() == 0.0
											&& maxlight - oldLightsOn >= 1) {
										turnOn(dimmerLight);
										;
										oldLightsOn++;
									} else if (sens.getSensedPresence() && maxlight - oldLightsOn < 1
											&& maxlight - oldLightsOn > 0) {
										dimmerLight.setPowerLevel((maxlight - oldLightsOn));
										oldLightsOn = (int) maxlight;
									} else {
										turnOff(dimmerLight);
									}

								}
							}
						}

					} else {
						changingLight.turnOff();
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

						if (dimmerConfig(lightLocation) < maxlight)
							maxlight = dimmerConfig(lightLocation);

						for (GenericDevice light : lightsInTheRoom) {
							if (isOn(light)) {
								lightsOn++;
							}
						}

						for (PresenceSensor presenceSensor : sameLocationPresenceSensors) {
							// and switch the light on/off depending on the sensed presence in the room
							if (presenceSensor.getSensedPresence() && changingLight.getPowerLevel() == 0.0
									&& maxlight - lightsOn >= 1) {
								changingLight.setPowerLevel(1);
								lightsOn++;
							} else {
								changingLight.setPowerLevel(0);
							}
						}

						List<BinaryLight> oldBLights = getBLightFromLocation(oldValue.toString());
						List<PresenceSensor> oldPresSens = getPresenceSensorFromLocation(oldValue.toString());
						List<DimmerLight> oldDLights = getDLightFromLocation(oldValue.toString());
						List<GenericDevice> oldLights = getLightFromLocation(oldValue.toString());

						if (dimmerConfig(oldValue.toString()) < oldmaxlight)
							maxlight = dimmerConfig(oldValue.toString());
						else
							maxlight = oldmaxlight;

						for (PresenceSensor sens : oldPresSens) {
							for (GenericDevice light : oldLights) {
								if (isOn(light)) {
									turnOff(light);
								}

								for (BinaryLight binaryLight : oldBLights) {
									if (sens.getSensedPresence() && oldLightsOn < Math.floor(maxlight)) {
										if (!isOn(binaryLight)) {
											turnOn(binaryLight);
											;
											oldLightsOn++;
										}
									}
								}
								for (DimmerLight dimmerLight : oldDLights) {

									if (sens.getSensedPresence() && dimmerLight.getPowerLevel() == 0.0
											&& maxlight - oldLightsOn >= 1) {
										turnOn(dimmerLight);
										;
										oldLightsOn++;
									} else if (sens.getSensedPresence() && maxlight - oldLightsOn < 1
											&& maxlight - oldLightsOn > 0) {
										dimmerLight.setPowerLevel((maxlight - oldLightsOn));
										oldLightsOn = (int) maxlight;
									} else {
										turnOff(dimmerLight);
									}

								}
							}
						}

					} else {
						changingLight.setPowerLevel(0);
					}
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

	private synchronized List<BinaryLight> getBLightFromLocation(String location) {
		List<BinaryLight> lightLocation = new ArrayList<BinaryLight>();
		for (BinaryLight binLight : binaryLights) {
			if (binLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				lightLocation.add(binLight);
			}
		}
		return lightLocation;
	}

	private synchronized List<DimmerLight> getDLightFromLocation(String location) {
		List<DimmerLight> lightLocation = new ArrayList<DimmerLight>();
		for (DimmerLight dimLi : dimmerLights) {
			if (dimLi.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				lightLocation.add(dimLi);
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
			if (((DimmerLight) light).getPowerLevel() != 0.0) {
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
		double numberMax = maximumEnergyConsumptionAllowedInARoom / 100d;
		double lmax;
		double lightsOn = 0d;
		ArrayList<String> locations = new ArrayList<String>();
		maxLightsToTurnOnPerRoom = maximumNumberOfLightsToTurnOn;

		for (PresenceSensor sens : presenceSensors) {
			if (sens.getSensedPresence()) {
				locations.add(sens.getPropertyValue(LOCATION_PROPERTY_NAME).toString());
			}
		}
		for (String loc : locations) {

			if (maximumNumberOfLightsToTurnOn > numberMax)
				lmax = numberMax;
			else
				lmax = maximumNumberOfLightsToTurnOn;

			if (dimmerConfig(loc) < lmax)
				lmax = dimmerConfig(loc);

			List<BinaryLight> lights = getBLightFromLocation(loc);
			for (BinaryLight binaryLight : lights) {
				binaryLight.turnOff();
			}
			List<DimmerLight> dimmers = getDLightFromLocation(loc);
			for (DimmerLight dimmerLight : dimmers) {
				dimmerLight.setPowerLevel(0.0);
			}
			for (BinaryLight binaryLight : lights) {
				if (lightsOn < Math.floor(lmax)) {
					binaryLight.turnOn();
					lightsOn++;
				}
			}
			for (DimmerLight dimmerLight : dimmers) {
				if (dimmerLight.getPowerLevel() == 0.0 && lmax - lightsOn >= 1) {
					dimmerLight.setPowerLevel(1.0);
					lightsOn++;
				} else if (lmax - lightsOn < 1 && lmax - lightsOn > 0) {
					dimmerLight.setPowerLevel((lmax - lightsOn));
					lightsOn = lmax;
				}
			}
			lightsOn = 0d;
		}

	}

	@Override
	public double getMaximumAllowedEnergyInRoom() {
		return maximumEnergyConsumptionAllowedInARoom;
	}

	@Override
	public void setMaximumAllowedEnergyInRoom(double maximumEnergy) {
		this.maximumEnergyConsumptionAllowedInARoom = maximumEnergy;
		double numberMax = maximumEnergy / 100;
		double lmax;
		double lightsOn = 0d;
		ArrayList<String> locations = new ArrayList<String>();

		for (PresenceSensor sens : presenceSensors) {
			if (sens.getSensedPresence()) {
				locations.add(sens.getPropertyValue(LOCATION_PROPERTY_NAME).toString());
			}
		}
		System.out.println(locations.toString());
		for (String loc : locations) {
			if (maxLightsToTurnOnPerRoom > numberMax)
				lmax = numberMax;
			else
				lmax = maxLightsToTurnOnPerRoom;

			if (dimmerConfig(loc) < lmax)
				lmax = dimmerConfig(loc);

			List<BinaryLight> lights = getBLightFromLocation(loc);
			for (BinaryLight binaryLight : lights) {
				binaryLight.turnOff();
			}
			List<DimmerLight> dimmers = getDLightFromLocation(loc);
			for (DimmerLight dimmerLight : dimmers) {
				dimmerLight.setPowerLevel(0.0);
			}
			for (BinaryLight binaryLight : lights) {
				if (lightsOn < Math.floor(lmax)) {
					binaryLight.turnOn();
					lightsOn += 1.0;
				}
			}
			for (DimmerLight dimmerLight : dimmers) {
				if (dimmerLight.getPowerLevel() == 0.0 && lmax - lightsOn >= 1) {
					dimmerLight.setPowerLevel(1.0);
					lightsOn += 1.0;
				} else if (lmax - lightsOn < 1 && lmax - lightsOn > 0) {
					dimmerLight.setPowerLevel((lmax - lightsOn));
					lightsOn = lmax;
				}
			}

			lightsOn = 0d;
		}
		for (DimmerLight dim : dimmerLights) {
			System.out.println("POWER = " + dim.getPowerLevel());
		}

	}

	public double dimmerConfig(String location) {
		double ret = (zoneSizeCalculator.getSurfaceInMeterSquare(location) * targetedIlluminance)
				/ (ONE_WATT_TO_ONE_LUMEN * 100d);

		System.out.println("SURFACE= " + zoneSizeCalculator.getSurfaceInMeterSquare(location));
		System.out.println("ret= " + ret);
		return ret;
	}

	@Override
	public double getTargetedIlluminance() {
		return targetedIlluminance;
	}

	@Override
	public void setTargetedIlluminance(double illuminance) {
		targetedIlluminance = illuminance;

	}

}
