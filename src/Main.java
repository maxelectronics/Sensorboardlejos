import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.util.Delay;

/**
 * Java-Class to test the Sensorboard
 * Created by Max (max.electronics-at-me.com),
 * 
 * License: GPL 2.0
 *
 * This only refers to the 2nd Version, using 5 I2C-color-sensors and an ATtiny261A (probably...)
 * For the 1st Version, please refer to Andi...
 *
 * This code is self-documenting (or I'm just too lazy to write comments)
 */
public class Main {
	public static void main(String args[]) {
		int centre = 0;
		int sides = 0;
		int gain = 1;
		Sensorboard sensor = new Sensorboard(SensorPort.S1);
		
		int menu = 0;
		int sel = 0;
		
		while(true) {
			switch(menu) {
			case 0:
				LCD.clear();
				LCD.drawString("Sensor ID:", 0, 0);
				LCD.drawInt(sensor.getDeviceID(), 3, 13, 0);
				LCD.drawString("Brightness:", 0, 2);
				LCD.drawString("Centre:", 1, 3);
				LCD.drawString("Sides:", 1, 4);
				LCD.drawString("Gain:", 1, 5);
				LCD.drawString("Show values", 1, 7);
				LCD.drawInt(centre, 3, 12, 3);
				LCD.drawInt(sides, 3, 12, 4);
				LCD.drawInt(gain, 2, 13, 5);
				
				if(sel < 3) LCD.drawChar('>', 0, sel+3);
				else if(sel == 3) LCD.drawChar('>', 0, 7);
				else LCD.drawChar('<', 15, sel-1);
				
				switch(Button.waitForAnyPress()) {
				case Button.ID_ENTER:
					if(sel < 3) sel += 4;
					else if(sel > 3) sel -= 4;
					else if(sel == 3) {
						menu = 1;
						sel = 0;
					}
					break;
				case Button.ID_LEFT:
					if(sel == 4 && centre > 0) {
						centre--;
						LCD.drawInt(centre, 3, 12, 3);
						sensor.setBrightness(centre, sides);
						Delay.msDelay(500);
						while(Button.LEFT.isDown() && centre > 0) {
							centre--;
							LCD.drawInt(centre, 3, 12, 3);
							sensor.setBrightness(centre, sides);
							Delay.msDelay(20);
						}
					} else if(sel == 5 && sides > 0) {
						sides--;
						LCD.drawInt(sides, 3, 12, 4);
						sensor.setBrightness(centre, sides);
						Delay.msDelay(500);
						while(Button.LEFT.isDown() && sides > 0) {
							sides--;
							LCD.drawInt(sides, 3, 12, 4);
							sensor.setBrightness(centre, sides);
							Delay.msDelay(20);
						}
					} else if(sel == 6) {
						switch(gain) {
						case 4:
							gain = 1;
							break;
						case 16:
							gain = 4;
							break;
						case 60:
							gain = 16;
							break;
						}
						sensor.setSensorGain(gain);
					} else if(sel > 0 && sel < 4) sel--;
					break;
				case Button.ID_RIGHT:
					if(sel < 3) sel++;
					else if(sel == 4 && centre < 255) {
						centre++;
						LCD.drawInt(centre, 3, 12, 3);
						sensor.setBrightness(centre, sides);
						Delay.msDelay(500);
						while(Button.RIGHT.isDown() && centre < 255) {
							centre++;
							LCD.drawInt(centre, 3, 12, 3);
							sensor.setBrightness(centre, sides);
							Delay.msDelay(20);
						}
					} else if(sel == 5 && sides < 255) {
						sides++;
						LCD.drawInt(sides, 3, 12, 4);
						sensor.setBrightness(centre, sides);
						Delay.msDelay(500);
						while(Button.RIGHT.isDown() && sides < 255) {
							sides++;
							LCD.drawInt(sides, 3, 12, 4);
							sensor.setBrightness(centre, sides);
							Delay.msDelay(20);
						}
							
					} else if(sel == 6) {
						switch(gain) {
						case 1:
							gain = 4;
							break;
						case 4:
							gain = 16;
							break;
						case 16:
							gain = 60;
							break;
						}
						sensor.setSensorGain(gain);
					}
					break;
				case Button.ID_ESCAPE:
					if(sel > 3) sel -= 4;
					else System.exit(0);
					break;
				}
				break;
			case 1:
				LCD.clear();
				LCD.drawString("Show all", 1, 0);
				LCD.drawString("Show red", 1, 1);
				LCD.drawString("Show blue", 1, 2);
				LCD.drawString("Show green", 1, 3);
				LCD.drawString("Show clear", 1, 4);
				LCD.drawString("By Sensor:", 0, 6);
				LCD.drawString("0  1  2  3  4", 2, 7);
				
				if(sel < 5) LCD.drawChar('>', 0, sel);
				else LCD.drawChar('>', sel*3-14, 7);
				
				switch(Button.waitForAnyPress()) {
				case Button.ID_ENTER:
					menu = sel+2;
					break;
				case Button.ID_LEFT:
					if(sel > 0) sel--;
					break;
				case Button.ID_RIGHT:
					if(sel < 9) sel++;
					break;
				case Button.ID_ESCAPE:
					menu = 0;
					sel = 3;
					break;
				}
				break;
			case 2:
				LCD.clear();
				LCD.drawString("RGBC", 0, 1);
				LCD.drawString("0..4", 0, 2);
				
				while(Button.ESCAPE.isUp()) {
					Sensorboard.Color color[] = sensor.getAllSensorColors();
					int adcs[] = sensor.getADCs();
					LCD.drawInt(adcs[0], 3, 0, 4);
					LCD.drawInt(adcs[1], 3, 0, 5);
					boolean pins[] = sensor.getPins();
					if(pins[0]) LCD.drawInt(1, 0, 7);
					else LCD.drawInt(0, 0, 7);
					if(pins[1]) LCD.drawInt(1, 2, 7);
					else LCD.drawInt(0, 2, 7);
					for(int i = 0; i < 5; i++) {
						for(int j=0; j<color[i].red/1024; j++) LCD.setPixel(40+i*10, 64-j, 1);
						for(int j=color[i].red/1024; j<64; j++) LCD.setPixel(40+i*10, 64-j, 0);

						for(int j=0; j<color[i].green/1024; j++) LCD.setPixel(42+i*10, 64-j, 1);
						for(int j=color[i].green/1024; j<64; j++) LCD.setPixel(42+i*10, 64-j, 0);

						for(int j=0; j<color[i].blue/1024; j++) LCD.setPixel(44+i*10, 64-j, 1);
						for(int j=color[i].blue/1024; j<64; j++) LCD.setPixel(44+i*10, 64-j, 0);

						for(int j=0; j<color[i].clear/1024; j++) LCD.setPixel(46+i*10, 64-j, 1);
						for(int j=color[i].clear/1024; j<64; j++) LCD.setPixel(46+i*10, 64-j, 0);
					}
					for(int j=0; j<adcs[0]/4; j++) LCD.setPixel(96, 64-j, 1);
					for(int j=adcs[0]/4; j<64; j++) LCD.setPixel(96, 64-j, 0);

					for(int j=0; j<adcs[1]/4; j++) LCD.setPixel(98, 64-j, 1);
					for(int j=adcs[1]/4; j<64; j++) LCD.setPixel(98, 64-j, 0);
				} Button.ESCAPE.waitForPress();
				menu = 1;
				sel = 0;
				break;
			case 3:
			case 4:
			case 5:
			case 6:
				LCD.clear();
				LCD.drawString("0..4", 0, 1);
				
				while(Button.ESCAPE.isUp()) {
					int[] sensors = new int[5];
					switch(menu) {
					case 3: sensors = sensor.getRed(); break;
					case 4: sensors = sensor.getGreen(); break;
					case 5: sensors = sensor.getBlue(); break;
					case 6: sensors = sensor.getClear(); break;
					}
					for(int i = 0; i < 5; i++) {
						LCD.drawInt(sensors[i], 5, 0, i+2);
						
						for(int j=0; j<sensors[i]/1024; j++) LCD.setPixel(40+i*8, 64-j, 1);
						for(int j=sensors[i]/1024; j<64; j++) LCD.setPixel(40+i*8, 64-j, 0);
					}
				} Button.ESCAPE.waitForPress();
				sel = menu-2;
				menu = 1;
				break;
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
				LCD.clear();
				LCD.drawString("RGBC", 0, 0);
				
				while(Button.ESCAPE.isUp()) {
					Sensorboard.Color color = sensor.getSensorColor(menu - 7);
					LCD.drawInt(color.red, 5, 0, 2);
					LCD.drawInt(color.blue, 5, 0, 3);
					LCD.drawInt(color.green, 5, 0, 4);
					LCD.drawInt(color.clear, 5, 0, 5);
					
					for(int j=0; j<color.red/1024; j++) LCD.setPixel(40, 64-j, 1);
					for(int j=color.red/1024; j<64; j++) LCD.setPixel(40, 64-j, 0);
					
					for(int j=0; j<color.green/1024; j++) LCD.setPixel(48, 64-j, 1);
					for(int j=color.green/1024; j<64; j++) LCD.setPixel(48, 64-j, 0);
					
					for(int j=0; j<color.blue/1024; j++) LCD.setPixel(56, 64-j, 1);
					for(int j=color.blue/1024; j<64; j++) LCD.setPixel(56, 64-j, 0);
					
					for(int j=0; j<color.clear/1024; j++) LCD.setPixel(64, 64-j, 1);
					for(int j=color.clear/1024; j<64; j++) LCD.setPixel(64, 64-j, 0);
				} Button.ESCAPE.waitForPress();
				sel = menu-2;
				menu = 1;
				break;
			}
		}
	}
}
