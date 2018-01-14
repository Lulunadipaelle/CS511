package org.example.follow.me.manager.command;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;
import org.example.follow.me.manager.EnergyGoal;
import org.example.follow.me.manager.FollowMeAdministration;
import org.example.follow.me.manager.IlluminanceGoal;
//import com.example.follow.me.configuration.FollowMeConfiguration;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;

//Define this class as an implementation of a component :
@Component
//Create an instance of the component
@Instantiate(name = "follow.me.manager.command")
//Use the handler command and declare the command as a command provider. The
//namespace is used to prevent name collision.
@CommandProvider(namespace = "followme")
public class FollowMeManagerCommandImpl {

	// Declare a dependency to a FollowMeAdministration service
	@Requires
	private FollowMeAdministration m_administrationService;

	/**
	 * Felix shell command implementation to sets the illuminance preference.
	 *
	 * @param goal the new illuminance preference ("SOFT", "MEDIUM", "FULL")
	 */

	// Each command should start with a @Command annotation
	@Command
	public void setIlluminancePreference(String goal) {
		// The targeted goal
		IlluminanceGoal illuminanceGoal;
		// TODO : Here you have to convert the goal string into an illuminance
		// goal and fail if the entry is not "SOFT", "MEDIUM" or "HIGH"
		System.out.println("goal entered : " + goal);
		if (goal.equals("SOFT")) {
			illuminanceGoal = IlluminanceGoal.SOFT;
			m_administrationService.setIlluminancePreference(illuminanceGoal);
		} else if (goal.equals("MEDIUM")) {
			illuminanceGoal = IlluminanceGoal.MEDIUM;
			m_administrationService.setIlluminancePreference(illuminanceGoal);
		} else if (goal.equals("FULL")) {
			illuminanceGoal = IlluminanceGoal.FULL;
			m_administrationService.setIlluminancePreference(illuminanceGoal);
		} else {
			illuminanceGoal = IlluminanceGoal.SOFT;
			System.out.println("SetIlluminancePreference error : bad goal entered, please enter SOFT, MEDIUM or FULL");
			System.out.println("set to SOFT by default");
		}

	}

	@Command
	public void getIlluminancePreference() {
		System.out.println("The illuminance goal is "); //...
		IlluminanceGoal illuminanceGoal = m_administrationService.getIlluminancePreference();
		System.out.println(illuminanceGoal.toString());

	}
	
	@Command
	public void getEnergyGoal() {
		System.out.println("The current energy goal is : " + m_administrationService.getEnergyGoal());
	}
	
	@Command
	public void setEnergyGoal(String goal) {
		EnergyGoal energygoal;
		
		if (goal.equals(new String("LOW"))) {
			energygoal = EnergyGoal.LOW;
		} else if (goal.equals(new String("MEDIUM"))) {
			energygoal = EnergyGoal.MEDIUM;
		} else if (goal.equals(new String("HIGH"))) {
			energygoal = EnergyGoal.HIGH;
		} else {// forcé à soft en cas d'erreur
			energygoal = EnergyGoal.LOW;
			System.out.println("setMaximumEnergyAllowed error : bad goal entered, please enter LOW, MEDIUM or HIGH");
			System.out.println("set to LOW by default");
		}
		m_administrationService.setEnergyGoal(energygoal);
	}
	

	/** Component Lifecycle Method */
	public void stop() {
		// TODO: Add your implementation code here
		System.out.println("Component FollowMeManagerCommand is stopping...");
	}

	/** Component Lifecycle Method */
	public void start() {
		// TODO: Add your implementation code here
		System.out.println("Component FollowMeManagerCommand is starting...");
	}

}