package app;

import service.DriveService;
import utils.DriveUtils;
import validations.Validation;

public class App {
	
	// -------------------------------- Utils --------------------------------

	public static boolean functionManager(String... args) {
				boolean valid = true;
				
		if (args.length == 2) {
			if (Validation.validateArgs(args[0], null, args[1])) {
				DriveService.filesAndDirectoryUploader(null, args[1]);
			}
		} else if (args.length == 3) {
			if (Validation.validateArgs(args[0], args[1], args[2])) {
				if (args[0].equalsIgnoreCase("ul")) {
					DriveService.filesAndDirectoryUploader(DriveUtils.extractDriveId(args[1]), args[2]);
				} else if (args[0].equalsIgnoreCase("dl")) {
					DriveService.filesAndDirectoryDownloader(DriveUtils.extractDriveId(args[1]), args[2]);
				}
			}
		} else {
			valid = false;
			System.out.println("Error: Wrong number of Arguments!");
		}
		return valid;
	}

	// -------------------------------- Main --------------------------------

	public static void main(String... args) {
		System.out.println("### Drive File Manager App. Status: Started. ###");
		functionManager(args);
		System.out.println("###  Drive File Manager App. Status: Terminated. ###");
	}
}
