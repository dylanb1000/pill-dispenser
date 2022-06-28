import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.util.CommandArgumentParser;

public class Machine implements Runnable {
	private Queue<Medication> queue = new LinkedList<Medication>();
	private Model model;
	// steps motor needs to make 1 full revolution
	private int stepsForRotation;
	// initialize machine components
	private final GpioController gpio = GpioFactory.getInstance();
	// Main Servo
	private Pin pin0 = CommandArgumentParser.getPin(RaspiPin.class, RaspiPin.GPIO_00);
	private GpioPinPwmOutput pwm0 = gpio.provisionSoftPwmOutputPin(pin0);

	// Stepper
	final GpioPinDigitalOutput[] pins = { gpio.provisionDigitalOutputPin(RaspiPin.GPIO_21, PinState.LOW),
			gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, PinState.LOW),
			gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23, PinState.LOW),
			gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, PinState.LOW) };
	private GpioStepperMotorComponent motor = new GpioStepperMotorComponent(pins);
	// limit switch top
	private GpioPinDigitalInput input1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_UP);
	// limit switch bottom
	private GpioPinDigitalInput input2 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_UP);
	// 0.1 milliseconds
	private int sleep = 100;

	public Machine(Model model) {
		this.model = model;
		gpio.setShutdownOptions(true, PinState.LOW, pins);
		// define stepper parameters before attempting to control motor
		// anything lower than 2 ms does not work for my sample motor using single step
		// sequence
		motor.setStepInterval(2);
		motor.setStepSequence(singleStepSequence());
	}

	public Queue<Medication> getQueue() {
		return this.queue;
	}

	public void addToMedicationQueue(Medication med) {
		queue.add(med);
	}

	public void dispenseMedication() {
		Medication dispensedMed = queue.remove();
		int slotNumber = dispensedMed.getSlotNumber();
		rotateSlots(slotNumber);
		tipActive();
		tipToHome();
		Thread.sleep(1000);
		System.out.println("Dispensed: " + dispensedMed.getName());
		// log
		String name = dispensedMed.getName();
		String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		model.getUser().getLog().addEntry(name, time);
	}

	public void tipToHome() {
		while (input1.isLow()) {
			try {
				pwm0.setPwm(25);
				System.out.println("PWM rate is: " + pwm0.getPwm());
				Thread.sleep(sleep);
			} catch (Exception e) {
				System.out.println("cant");
			}
		}
	}

	public void tipActive() {
		while (input2.isLow()) {
			try {
				pwm0.setPwm(-25);
				System.out.println("PWM rate is: " + pwm0.getPwm());
				Thread.sleep(sleep);
			} catch (Exception e) {
				System.out.println("cant");
			}
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				dispenseMedication();
			} catch (Exception e) {

			}
		}
	}

	public void rotateSlots(int slotNumber) {
		int currentSlot = model.getCurrentSlot();
		int difference = currentSlot - slotNumber;
		motor.step((difference/ 6) * this.stepsForRotation);
		model.setCurrentSlot(slotNumber);
	}

	// There are 32 steps per revolution on my sample motor, and inside is a ~1/64
	// reduction gear set.
	// Gear reduction is actually: (32/9)/(22/11)x(26/9)x(31/10)=63.683950617
	// This means is that there are really 32*63.683950617 steps per revolution =
	// 2037.88641975 ~ 2038 steps!
	/*
	 * motor.setStepsPerRevolution(2038);
	 * 
	 * // test motor control : STEPPING FORWARD
	 * System.out.println("   Motor FORWARD for 2038 steps."); motor.step(2038);
	 * System.out.println("   Motor STOPPED for 2 seconds."); Thread.sleep(2000);
	 * 
	 * // test motor control : STEPPING REVERSE
	 * System.out.println("   Motor REVERSE for 2038 steps."); motor.step(-2038);
	 * System.out.println("   Motor STOPPED for 2 seconds."); Thread.sleep(2000);
	 */

	// create byte array to demonstrate a single-step sequencing
	// (This is the most basic method, turning on a single electromagnet every time.
	// This sequence requires the least amount of energy and generates the smoothest
	// movement.)
	public byte[] singleStepSequence() {
		byte[] single_step_sequence = new byte[4];
		single_step_sequence[0] = (byte) 0b0001;
		single_step_sequence[1] = (byte) 0b0010;
		single_step_sequence[2] = (byte) 0b0100;
		single_step_sequence[3] = (byte) 0b1000;
		return single_step_sequence;
	}

	// create byte array to demonstrate a double-step sequencing
	// (In this method two coils are turned on simultaneously. This method does not
	// generate
	// a smooth movement as the previous method, and it requires double the current,
	// but as
	// return it generates double the torque.)
	public byte[] doubleStepSequence() {
		byte[] double_step_sequence = new byte[4];
		double_step_sequence[0] = (byte) 0b0011;
		double_step_sequence[1] = (byte) 0b0110;
		double_step_sequence[2] = (byte) 0b1100;
		double_step_sequence[3] = (byte) 0b1001;
		return double_step_sequence;
	}

	// create byte array to demonstrate a half-step sequencing
	// (In this method two coils are turned on simultaneously. This method does not
	// generate
	// a smooth movement as the previous method, and it requires double the current,
	// but as
	// return it generates double the torque.)
	public byte[] halfStepSequence() {
		byte[] half_step_sequence = new byte[8];
		half_step_sequence[0] = (byte) 0b0001;
		half_step_sequence[1] = (byte) 0b0011;
		half_step_sequence[2] = (byte) 0b0010;
		half_step_sequence[3] = (byte) 0b0110;
		half_step_sequence[4] = (byte) 0b0100;
		half_step_sequence[5] = (byte) 0b1100;
		half_step_sequence[6] = (byte) 0b1000;
		half_step_sequence[7] = (byte) 0b1001;
		return half_step_sequence;
	}

	public void stop() {
		gpio.shutdown();
	}
}
