import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.util.CommandArgumentParser;

public class RackServo {
	private final GpioController gpio = GpioFactory.getInstance();
	private Pin pin = CommandArgumentParser.getPin(RaspiPin.class, RaspiPin.GPIO_00);
	private GpioPinPwmOutput pwm = gpio.provisionSoftPwmOutputPin(pin);
	private int sleep = 1000;

	public RackServo() {

	}

	public void goServo() {
		for (int i = 0; i < 10; i++) {
			try {
				pwm.setPwm(25);
				System.out.println("PWM rate is: " + pwm.getPwm());
				Thread.sleep(sleep);

				pwm.setPwm(15);
				System.out.println("PWM rate is: " + pwm.getPwm());
				Thread.sleep(sleep);

				pwm.setPwm(6);
				System.out.println("PWM rate is: " + pwm.getPwm());
				Thread.sleep(sleep);
			} catch (Exception e) {
				System.out.println("cant");
			}
		}
	}
}


	
	



