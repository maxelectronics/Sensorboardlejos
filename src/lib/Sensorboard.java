import lejos.nxt.I2CPort;

/**
 * Java-Class to access the sensorboard from an NXT with leJOS.
 * Created by Max (max.electronics-at-me.com),
 * 
 * License: GPL 2.0
 *
 * This only refers to the 2nd Version, using 5 I2C-color-sensors and an ATtiny261A (probably...)
 * For the 1st Version, please refer to Andi...
 *
 * This code is self-documenting (or I'm just too lazy to write comments)
 */
public class Sensorboard {
	/**
	 * A custom Color object used for the color sensors.
	 * Contains four public 16-bit values (0...65536) representing each color (red, green, blue and clear).
	 */
	public class Color {
		public int red;
		public int green;
		public int blue;
		public int clear;
		
		/**
		 * Constructor to create a Color object.
		 * @param red New red 16bit value (0...65535)
		 * @param green New green 16bit value (0...65535)
		 * @param blue New blue 16bit value (0...65535)
		 * @param clear New clear 16bit value (0...65535)
		 */
		public Color(int red, int green, int blue, int clear) {
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.clear = clear;
		}
	}
	
    private I2CPort port;
    
    /**
     * Initializes the SensorPort for use with the Sensorboard.
     * @param port The SensorPort to initialize
     */
    public Sensorboard(I2CPort port) {
        this.port = port;
        this.port.i2cEnable(0);
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
     * Reads all colors from all sensors.
     * @return An array containing five color objects, one for each sensor
     */
    public Color[] getAllSensorColors() {
    	Color out[] = new Color[5];
    	for(int i = 0; i < 5; i++)
    		out[i] = getSensorColor(i);
    	return out;
    }
    
    /**
     * Reads the red values from all Sensors.
     * @return An array containing five 16bit values (0...65535), one for each sensor
     */
    public int[] getRed() {
    	byte write[] = {0x5B};
    	byte read[] = new byte[10];
    	port.i2cTransaction(48, write, 0, 1, read, 0, 10);
    	return new int[] {
    			(int)(read[0] & 0xFF) + (int)(read[5] & 0xFF)*256,
    			(int)(read[1] & 0xFF) + (int)(read[6] & 0xFF)*256,
    			(int)(read[2] & 0xFF) + (int)(read[7] & 0xFF)*256,
    			(int)(read[3] & 0xFF) + (int)(read[8] & 0xFF)*256,
    			(int)(read[4] & 0xFF) + (int)(read[9] & 0xFF)*256};
    }

    /**
     * Reads the green values from all Sensors.
     * @return An array containing five 16bit values (0...65535), one for each sensor
     */
    public int[] getGreen() {
    	byte write[] = {0x5C};
    	byte read[] = new byte[10];
    	port.i2cTransaction(48, write, 0, 1, read, 0, 10);
    	return new int[] {
    			(int)(read[0] & 0xFF) + (int)(read[5] & 0xFF)*256,
    			(int)(read[1] & 0xFF) + (int)(read[6] & 0xFF)*256,
    			(int)(read[2] & 0xFF) + (int)(read[7] & 0xFF)*256,
    			(int)(read[3] & 0xFF) + (int)(read[8] & 0xFF)*256,
    			(int)(read[4] & 0xFF) + (int)(read[9] & 0xFF)*256};
    }

    /**
     * Reads the blue values from all Sensors.
     * @return An array containing five 16bit values (0...65535), one for each sensor
     */
    public int[] getBlue() {
    	byte write[] = {0x5D};
    	byte read[] = new byte[10];
    	port.i2cTransaction(48, write, 0, 1, read, 0, 10);
    	return new int[] {
    			(int)(read[0] & 0xFF) + (int)(read[5] & 0xFF)*256,
    			(int)(read[1] & 0xFF) + (int)(read[6] & 0xFF)*256,
    			(int)(read[2] & 0xFF) + (int)(read[7] & 0xFF)*256,
    			(int)(read[3] & 0xFF) + (int)(read[8] & 0xFF)*256,
    			(int)(read[4] & 0xFF) + (int)(read[9] & 0xFF)*256};
    }

    /**
     * Reads the clear values from all Sensors.
     * @return An array containing five 16bit values (0...65535), one for each sensor
     */
    public int[] getClear() {
    	byte write[] = {0x5A};
    	byte read[] = new byte[10];
    	port.i2cTransaction(48, write, 0, 1, read, 0, 10);
    	return new int[] {
    			(int)(read[0] & 0xFF) + (int)(read[5] & 0xFF)*256,
    			(int)(read[1] & 0xFF) + (int)(read[6] & 0xFF)*256,
    			(int)(read[2] & 0xFF) + (int)(read[7] & 0xFF)*256,
    			(int)(read[3] & 0xFF) + (int)(read[8] & 0xFF)*256,
    			(int)(read[4] & 0xFF) + (int)(read[9] & 0xFF)*256};
    }

    /**
     * Reads all colors from one color sensor
     * @param sensor Specifies the color sensor to read (0...4)
     * @return A new Color object containing the four colors
     */
    public Color getSensorColor(int sensor) {
    	if(sensor < 0 || sensor > 4) return null;
    	byte write[] = {(byte)(0x55 + sensor)};
    	byte read[] = new byte[8];
    	port.i2cTransaction(48, write, 0, 1, read, 0, 8);
    	return new Color(
    			(int)(read[2] & 0xFF) + (int)(read[3] & 0xFF)*256,
    			(int)(read[4] & 0xFF) + (int)(read[5] & 0xFF)*256,
    			(int)(read[6] & 0xFF) + (int)(read[7] & 0xFF)*256,
    			(int)(read[0] & 0xFF) + (int)(read[1] & 0xFF)*256);
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
