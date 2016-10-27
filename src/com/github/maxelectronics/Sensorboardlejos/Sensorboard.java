import lejos.nxt.I2CPort;
import lejos.util.Delay;

/**
 * Java-Class to access the sensorboard from an NXT with leJOS.
 * Created by Max (max.electronics-at-me.com).
 * 
 * This class is intended to be used in your projects as-is. DON'T change anything!
 * 
 * License: GPL 2.0
 *
 * This only refers to the 2nd Version, using 5 I2C-color-sensors and an ATtiny261A (probably...)
 * For the 1st Version, please refer to Andi...
 *
 * This code is self-documenting (or I'm just too lazy to write comments)
 */
public class Sensorboard extends Thread {
	/**
	 * A custom Color object used for the color sensors.
	 * Contains four public values (0...1024 at default timing) representing each color (red, green, blue and clear).
	 */
	public class Color {
		public int red;
		public int green;
		public int blue;
		public int clear;
		
		/**
		 * Constructor to create a Color object.
		 * @param red New red value (0...1024 at default timing)
		 * @param green New green value (0...1024 at default timing)
		 * @param blue New blue value (0...1024 at default timing)
		 * @param clear New clear value (0...1024 at default timing)
		 */
		public Color(int red, int green, int blue, int clear) {
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.clear = clear;
		}
	}
	
    private I2CPort port;
    private Color allSensors[];
    
    /**
     * Initializes the SensorPort for use with the Sensorboard.
     * @param port The SensorPort to initialize
     */
    public Sensorboard(I2CPort port) {
        this.port = port;
        this.port.i2cEnable(10);
        
        this.start();
    }
    
    /**
     * Initializes the SensorPort for use with the Sensorboard.
     * @param port The SensorPort to initialize
     * @param brightness Brightness of the LEDs
     * @param gain Sensor gain, accepted values are 1, 4, 16, 60
     * @param cycles Sensor Timing, less cycles means higher sample rate (default: 1)
     */
    public Sensorboard(I2CPort port, int brightness, int gain, int cycles) {
    	this(port);
    	setBrightness(brightness, brightness);
    	setSensorGain(gain);
    	setTiming(cycles);
    }
    
    /**
     * Run method of this thread. Gets all the sensor data off the I2C bus periodically.
     */
    public void run() {
    	while(true) {
	    	Color sensors[] = new Color[5];
	    	for(int i = 0; i < 5; i++) {
	    		byte write[] = {(byte)(0x55 + i)};
	    		byte read[] = new byte[8];
	    		port.i2cTransaction(48, write, 0, 1, read, 0, 8);
	    		sensors[i] = new Color(
		    			(int)(read[2] & 0xFF) + (int)(read[3] & 0xFF)*256,
		    			(int)(read[4] & 0xFF) + (int)(read[5] & 0xFF)*256,
		    			(int)(read[6] & 0xFF) + (int)(read[7] & 0xFF)*256,
		    			(int)(read[0] & 0xFF) + (int)(read[1] & 0xFF)*256);
	    	}
	    	allSensors = sensors;
	    	Delay.msDelay(1);
    	}
    }
    
    /**
     * Reads the device ID of the color sensors.
     * There are two sensor types:
     * - 68 (HEX: 0x44): TCS34725, with IR filter
     * - 20 (HEX: 0x14): TCS34715, without IR filter
     * @return The device ID as integer
     */
    public int getDeviceID() {
    	byte write[] = {0x52};
    	byte read[] = new byte[1];
    	port.i2cTransaction(48, write, 0, 1, read, 0, 1);
    	return read[0];
    }
    
    /**
     * Sets the brightness of the LEDs. Use values between 0 (off) and 255 (full brightness).
     * @param center Brightness of the three center LEDs
     * @param sides Brightness of the two outer LEDs
     */
    public void setBrightness(int center, int sides) {
    	if(center > 255) center = 255;
    	else if(center < 0) center = 0;
    	if(sides > 255) sides = 255;
    	else if(sides < 0) sides = 0;
    	byte write[] = {0x53, (byte)(center & 0xFF), (byte)(sides)};
    	port.i2cTransaction(48, write, 0, 3, null, 0, 0);
    }
    
    /**
     * Sets the gain of the color sensors.
     * @param gain Accepted values are 1, 4, 16 and 60
     */
    public void setSensorGain(int gain) {
    	byte write[] = {0x54, 0};
    	switch(gain) {
    	case 1: write[1] = 0; break;
    	case 4: write[1] = 1; break;
    	case 16: write[1] = 2; break;
    	case 60: write[1] = 3; break;
    	default: return;
    	}
    	port.i2cTransaction(48, write, 0, 2, null, 0, 0);
    }
    
    /**
     * Sets the timing for each sample.
     * @param cycles Less cycles means higher sample rate, but lower output reading (default: 1)
     */
    public void setTiming(int cycles) {
    	if(cycles > 256) cycles = 1;
    	else if(cycles < 1) cycles = 255;
    	else cycles = 256-cycles;
    	byte write[] = {0x51, (byte)(cycles & 0xFF)};
    	port.i2cTransaction(48, write, 0, 2, null, 0, 0);
    }
    
    /**
     * Reads all colors from all sensors.
     * @return An array containing five color objects, one for each sensor
     */
    public Color[] getAllSensorColors() {
    	return allSensors;
    }
    
    /**
     * Reads the red values from all Sensors.
     * @return An array containing five values (0...1024 at default timing), one for each sensor
     */
    public int[] getRed() {
    	return new int[] {
    			allSensors[0].red,
    			allSensors[1].red,
    			allSensors[2].red,
    			allSensors[3].red,
    			allSensors[4].red
    	};
    }

    /**
     * Reads the green values from all Sensors.
     * @return An array containing five values (0...1024 at default timing), one for each sensor
     */
    public int[] getGreen() {
    	return new int[] {
    			allSensors[0].green,
    			allSensors[1].green,
    			allSensors[2].green,
    			allSensors[3].green,
    			allSensors[4].green
    	};
    }

    /**
     * Reads the blue values from all Sensors.
     * @return An array containing five values (0...1024 at default timing), one for each sensor
     */
    public int[] getBlue() {
    	return new int[] {
    			allSensors[0].blue,
    			allSensors[1].blue,
    			allSensors[2].blue,
    			allSensors[3].blue,
    			allSensors[4].blue
    	};
    }

    /**
     * Reads the clear values from all Sensors.
     * @return An array containing five values (0...1024 at default timing), one for each sensor
     */
    public int[] getClear() {
    	return new int[] {
    			allSensors[0].clear,
    			allSensors[1].clear,
    			allSensors[2].clear,
    			allSensors[3].clear,
    			allSensors[4].clear
    	};
    }

    /**
     * Reads all colors from one color sensor
     * @param sensor Specifies the color sensor to read (0...4)
     * @return A new Color object containing the four colors
     */
    public Color getSensorColor(int sensor) {
    	return allSensors[sensor];
    }
    
    /**
     * Reads the two analog 8bit values (0...255) from the pin headers.
     * @return An array containing the two values.
     */
    public int[] getADCs() {
    	byte write[] = {0x5E};
    	byte read[] = new byte[2];
    	port.i2cTransaction(48, write, 0, 1, read, 0, 2);
    	return new int[] {read[0] & 0xFF, read[1] & 0xFF};
    }
    
    /**
     * Reads the two boolean values from the pin headers.
     * @return An array containing the two values.
     */
    public boolean[] getPins() {
    	byte write[] = {0x5F};
    	byte read[] = new byte[1];
    	port.i2cTransaction(48, write, 0, 1, read, 0, 1);
    	boolean out[] = new boolean[2];
    	if((read[0]&0x20) == 0x20) out[1] = true;
    	if((read[0]&0x01) == 0x01) out[0] = true;
    	return out;
    }
}
