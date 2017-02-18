package org.usfirst.frc.team2261.robot;

/*----------------------------------------------------------------------------*/

/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
// PLEZ READ\ 
/* CLEAN IF YOU CANNOT CONNECT TO THE ROBORIO. HOW TO CLEAN: Go to Project > Clean to clean up project; may be able to work fine after.
 Another way to clean the roboRIO/DNS: Press Windows key > Search for Command Prompt > in the Prompt type in "ping" (roboRIO-2261-FRC.local)
or (10.22.61.78 to see if you can get a reply from it; After that use the command "ipconfig /flushdns" to clear out the Cache; problem
should be fixed to build the code. */
// import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SampleRobot;
//import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

import java.util.Date;

// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.first.wpilibj.vision
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
//import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
//Added new declaration to import box "edu.wpi.first.wpilibj.vision.USBCamera" to box.
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends SampleRobot {

	// private static final double DEADZONE = -0.3;
	static double UPDATE_DELAY = 0.0075; // How often to refresh
	static double MOTOR_EXPIRATION = UPDATE_DELAY * 4;
	//static double CLAW_UP = 3.0;
	//static double CLAW_DOWN = 0.5;
	Command autonomousCommand;
	

	// Dampening constants
	// Not used until we have ability to do so.        NOT USED INDEFINITLY - REASONS
	/* private static final double kPWM = 0.005; // added .005 to 0; hopefully able
												// to have increased driving
												// ability.
	private static final double kPWM_MAX = 0.5; // dropped 1 down to 0.1; made
												// the driving speed have less
												// turning radius.
	private static final double kdPWM_MAX = 0.5; */         

	// IMPORTANT!!!!!!
	// Set to true for driving fork_lift from accessory y-axis (when not at
	// limit or being driven by accessory buttons)
	static boolean MAP_YAXIS_TO_FORKLIFT = true;
	static boolean MAP_ZAXIS_TO_ClAW = true;
	static boolean DIP_SWITCH = true;

	// Values for fork_lift motor (determined by testing)
	//static double FORKLIFT_NOMINAL_UP = 0.2;
	//static double FORKLIFT_ALOT_UP = 0.2;
	//static double FORKLIFT_FULL_LIFT = 0.75;
	//static double FORKLIFT_NOMINAL_DOWN = -0.5;
	//static double CLAW_OPEN = 0.5;
	//static double CLAW_CLOSE = -0.5;
	//static double KICK_DOWN = -0.9;// ARM Added much more added effect to
									// Kicker; originally -.01 and added to
									// -.09.
//	static double KICK_UP = 1.0;// DISARM Added much more added effect to
								// Kicker; originally .01 and added to .09.
	static double WINCH_UP = 1;
	static double WINCH_DOWN = -1;
	
	// Create a new RobotDrive, composed of the following motor controllers
	//Talons (#1 is back right Talon, #3 is back left Talon) are used for driving.
	Spark Right = new Spark(4); // Undo'd the Talon.
	
	
	Spark Left = new Spark(3); // Undo'd the Talon. Changed from 3 to 4 to determine the Talon.
	
	
	 Spark winchMotor = new Spark(2); //used for Winch
	// Spark winchMotor = new Spark(2); 
	
	DoubleSolenoid BillygatesSolenoid = new DoubleSolenoid(0,1);
	//Solenoid GodsSolenoid = new Solenoid(3);
	//Talon frontLeft = new Talon(4);  // currently unused
	
	RobotDrive robotDrive = new RobotDrive(Left, Right); //frontLeft, frontRight);   - Changed backLeft and backRight to Left and Right
	
	Compressor compressor = new Compressor();

/*	Spark clawController = new Spark(5);

	Spark upAndDownController = new Spark(6);

	Spark kickerController = new Spark(7); // Sparks number 7 and number 8 are
											// shared.

*/ //Create new Joy_stick on ports 1 and 2
	Joystick driverJoystick = new Joystick(0);
	Joystick accessoryJoystick = new Joystick(1);
	static double prevPWM, dPWM = 0;

	// Kicker_Claw Sensor
/*	DigitalInput forkliftLowerSwitch = new DigitalInput(2);
	DigitalInput forkliftUpperSwitch = new DigitalInput(3);
	DigitalInput kickerSwitch = new DigitalInput(0);
	DigitalInput forkliftSwitch = new DigitalInput(3);
*/
	boolean robotInitted = false;
//	boolean useForklift = false;

	protected void robotInit() {
		
	        CameraServer.getInstance().startAutomaticCapture();  // cam0
	        CameraServer.getInstance().startAutomaticCapture();  // cam2 - shows up in driver station options as Camera 1
		// (Re-)Enable motor safety with .25s expiration
		// "Motor safety is a mechanism built into the RobotDrive object that
		// will turn off the motors if the program doesn't continuously update
		// the motor speed." (default "expiration" is 100ms)
		robotDrive.setExpiration(Robot.MOTOR_EXPIRATION);  
		robotDrive.setSafetyEnabled(true);

		compressor.start();
		
		// Ensure all motors are stopped (I don't believe we should have to do this)
		robotDrive.drive(0, 0);
// ToDo Add Winchdrive zero out here
		robotInitted = true;
	}

	public void autonomous() 
	{

		robotDrive.setExpiration(MOTOR_EXPIRATION);
		if (!robotInitted) 
		{
			System.out.println("Robot not initted? Initting...");
			robotInit();
		}
		
		robotDrive.drive(0.5, 0);
		Date startTime = new Date();
// Chris was here
//		while (isAutonomous() && isEnabled()) 
//		{
//			Date now = new Date();
//			while (now - startTime < 3000) {
//				autonomousPeriodic();
//			}
			autonomousPeriodic();
			DIP_SWITCH = true;
			if (DIP_SWITCH == true)
			{
	
			}
			// Driver forward at half speed for 3 seconds
			 
			// Timer.delay(3);
			// robotDrive.drive(0, 0);
			// Timer.delay(100);
		}
		//-New 2017 pseudocode
		// CENTER START
		// wait one second
		// move forward 1 rotation - est x amount of time
		// turn 90 degrees to right
		// 3 rotations forward
		// 90 degrees to left
		// 5 rotations forward
		/*robotDrive.drive(1, 1); //move forward
		Timer.delay(1);
		robotDrive.drive(0, 0);
		robotDrive.drive(1, 0); //turn right
		Timer.delay(2);
		robotDrive.drive(0, 0);
		robotDrive.drive(1, 1); //drive forward
		Timer.delay(3);
		robotDrive.drive(0, 0); 
		robotDrive.drive(0, 1); //turn left
		Timer.delay(2);
		robotDrive.drive(0, 0);
		robotDrive.drive(1, 1); //drive forward
		Timer.delay(5);
		robotDrive.drive(0, 0);
		
		// LEFT START
		// move forward 6 rotations-subject to change
		robotDrive.drive(1, 1);
		Timer.delay(6);
		robotDrive.drive(0, 0);
		*/
		// RIGHT START
		// move forward 6 rotations-subject to change
//temp disable		robotDrive.drive(1, 1);
//temp disable		Timer.delay(6);
//temp disable		robotDrive.drive(0, 0);
		
		
		// COMPLICATED RIGHT START
		// Move forward 112 inches forward
		// turn left 45 degrees
		// probably forward some inches
		// activate pneumatics
		// moving back some inches
		// deactivate pneumatics or just keep it as is
		
		//
		
		// COMPLICATED LEFT START
		// Move forward 112 inches forward
		// turn right 45 degrees
		// some inches forward
		// activate pneumatics
		// move back some inches
		// deactivate pneumatics or just keep it as is
		
		
		
	}

	/**
	 * This function is called once each time the robot enters operator control.
	 */
	public void operatorControl() {
		robotDrive.setExpiration(MOTOR_EXPIRATION);

		
		if (!robotInitted) {
			System.out.println("Robot not initted? Initting...");
			robotInit();
		}

		// While still under operator control and enabled, "arcade drive" robot,
		while (isOperatorControl() && isEnabled()) {

			robotDrive.arcadeDrive(driverJoystick.getZ() * 1, driverJoystick.getY() * 1); // Rotate Clockwise and Counter-Clockwise

			if (accessoryJoystick.getRawButton(6)) { // Winch Drive up
			//	
				BillygatesSolenoid.set(DoubleSolenoid.Value.kForward);
			}
			else if(accessoryJoystick.getRawButton(5)) {
				BillygatesSolenoid.set(DoubleSolenoid.Value.kReverse);
			}
			else if(accessoryJoystick.getRawButton(4)) {
				BillygatesSolenoid.set(DoubleSolenoid.Value.kOff);
			}

			if (accessoryJoystick.getRawButton(8)) { // Winch Drive up
			//	
				winchMotor.set(Robot.WINCH_UP);
			}
			else {
				winchMotor.set(0);   //stop winch
			}
			
			if (accessoryJoystick.getRawButton(7)){ //Winch Drive Down
				winchMotor.set(Robot.WINCH_DOWN);
			}
			else {
				winchMotor.set(0);  //stop winch
			}
			//	winchMotor.set((-accessoryJoystick.getRawButton(8)); }
			
			// Print debug info
			putSmartDashboardValues();

			Timer.delay(Robot.UPDATE_DELAY); // Wait the specified second before
												// updating again
		}

		// Stop the robot
		robotDrive.drive(0, 0);
	}
		
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}


	protected void disabled() {
		robotDrive.drive(0, 0);
		robotInitted = false;
		BillygatesSolenoid.set(DoubleSolenoid.Value.kOff);
		compressor.stop();

	}

	private void putSmartDashboardValues() {

		/*
		 * SmartDashboard.putBoolean("isKickerLoaded()", isKickerLoaded());
		 * SmartDashboard.putBoolean("isForkliftAtUpperLimit()",
		 * isForkliftAtUpperLimit());
		 * SmartDashboard.putNumber("upAndDownController.getRaw()",
		 * upAndDownController.getRaw());
		 * 
		 * SmartDashboard.putNumber("driverJoystick.getX()",
		 * driverJoystick.getX());
		 * SmartDashboard.putNumber("scaleJoystickInput(driverJoystick.getX())",
		 * scaleJoystickInput(driverJoystick.getX()));
		 * SmartDashboard.putNumber("driverJoystick.getY()",driverJoystick.getY(
		 * ));
		 * SmartDashboard.putNumber("scaleJoystickInput(driverJoystick.getY())",
		 * scaleJoystickInput(driverJoystick.getY()));
		 * SmartDashboard.putNumber("driverJoystick.getZ()",driverJoystick.getZ(
		 * ));
		 * SmartDashboard.putNumber("scaleJoystickInput(driverJoystick.getZ())",
		 * scaleJoystickInput(driverJoystick.getZ()));
		 * SmartDashboard.putNumber("driverJoystick.getThrottle()",
		 * driverJoystick.getThrottle());
		 * SmartDashboard.putNumber("accessoryJoystick.getX())",
		 * accessoryJoystick.getX());
		 * SmartDashboard.putNumber("accessoryJoystick.getY()",accessoryJoystick
		 * .getY()); SmartDashboard.putNumber(
		 * "scaleJoystickInput(accessoryJoystick.getY())",
		 * scaleJoystickInput(accessoryJoystick.getX()));
		 * SmartDashboard.putNumber("accessoryJoystick.getZ()",accessoryJoystick
		 * .getZ()); SmartDashboard.putNumber("accessoryJoystick.getThrottle()",
		 * accessoryJoystick.getThrottle());
		 * SmartDashboard.putNumber("accessoryJoystick.getTwist()",
		 * accessoryJoystick.getTwist());
		 * SmartDashboard.putBoolean("accessoryJoystick.getRawButton(2)",
		 * accessoryJoystick.getRawButton(2));
		 * SmartDashboard.putBoolean("accessoryJoystick.getRawButton(3)",
		 * accessoryJoystick.getRawButton(3));
		 */
		//SmartDashboard.putBoolean("isKickerLoaded()", isKickerLoaded());
		
	}

}
