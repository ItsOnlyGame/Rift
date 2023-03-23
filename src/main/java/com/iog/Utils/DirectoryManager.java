package com.iog.Utils;

import org.tinylog.Logger;

import java.io.File;

public class DirectoryManager {
	
	public static void generateFolders() {
		File configFolder = new File("./config");
		if (!configFolder.exists()) {
			if (!configFolder.mkdir()) {
				Logger.error("Couldn't create a folder for the guild settings. Check file write permissions!");
				System.exit(12);
			}
		}
		
		File guildsFolder = new File("./config/guilds");
		if (!guildsFolder.exists()) {
			if (!guildsFolder.mkdir()) {
				Logger.error("Couldn't create a folder for the guild settings. Check file write permissions!");
				System.exit(12);
			}
		}
	}
	
}
