package com.iog.Utils;

import discord4j.rest.util.Color;
import org.tinylog.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class Format {
	
	public static Color hexToColor(String hex) {
		java.awt.Color color = java.awt.Color.decode(hex);
		return Color.of(color.getRGB());
	}
	
	/**
	 * @param str Int value in string format
	 * @return the Integer
	 */
	public static Integer toInteger(String str) {
		Integer result = null;
		try {
			result = Integer.parseInt(str.trim());
		} catch (NumberFormatException e) {
			Logger.error(e, e.getMessage());
		}
		return result;
	}
	
	/**
	 * @param url a url
	 * @return True if the string is a url
	 */
	public static Boolean isUrl(String url) {
		try {
			new URL(url.trim());
		} catch (MalformedURLException ignored) {
			return false;
		}
		return true;
	}
	
	/**
	 * @param str Float value in string format
	 * @return the Float
	 */
	public static Float toFloat(String str) {
		Float result = null;
		try {
			result = Float.parseFloat(str.trim());
		} catch (NumberFormatException e) {
			Logger.error(e, e.getMessage());
		}
		return result;
	}
	
	/**
	 * Formats a long value of milliseconds into hours, minutes and seconds
	 *
	 * @param milliseconds the time value in milliseconds
	 * @return Returns Formatted string value that represents hours, minutes and seconds
	 */
	public static String millisecondsToString(long milliseconds) {
		long hours = (milliseconds / 1000) / 3600;
		long minutes = ((milliseconds / 1000) % 3600) / 60;
		long seconds = ((milliseconds / 1000) % 60);
		
		if (hours >= 1) {
			return String.format("%d:%02d:%02d", hours, minutes, seconds);
		} else {
			return String.format("%d:%02d", minutes, seconds);
		}
	}
	
	/**
	 * Formats string into hours, minutes and seconds
	 *
	 * @param milliseconds the string to format
	 * @return Returns a Long value as that represents the time in milliseconds
	 */
	public static Long timestampToMillis(String milliseconds) {
		String[] time = milliseconds.split(":");
		long millisecond = 0;
		
		if (time.length == 3) {
			Integer hours = Format.toInteger(time[0]);
			Integer minutes = Format.toInteger(time[1]);
			Integer seconds = Format.toInteger(time[2]);
			
			if (hours == null || minutes == null || seconds == null) {
				return null;
			}
			
			millisecond += TimeUnit.HOURS.toMillis(hours);
			millisecond += TimeUnit.MINUTES.toMillis(minutes);
			millisecond += TimeUnit.SECONDS.toMillis(seconds);
			return millisecond;
		} else if (time.length == 2) {
			Integer minutes = Format.toInteger(time[0]);
			Integer seconds = Format.toInteger(time[1]);
			
			if (minutes == null || seconds == null) {
				return null;
			}
			
			millisecond += TimeUnit.MINUTES.toMillis(minutes);
			millisecond += TimeUnit.SECONDS.toMillis(seconds);
			return millisecond;
		} else {
			Integer seconds = Format.toInteger(time[0]);
			
			if (seconds == null) {
				return null;
			}
			
			millisecond += TimeUnit.SECONDS.toMillis(seconds);
			return millisecond;
		}
	}
}
