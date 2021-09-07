package validations;

import utils.DriveUtils;

public class Validation {
	
	// Valida los parámetros de ejecución recividos por el programa
	public static boolean validateArgs(String function, String drivePath, String path) {
		boolean valid = true;
		if (function == null || (!function.equalsIgnoreCase("dl") && !function.equalsIgnoreCase("ul"))) {
			valid = false;
			System.out.println("Error (Sintax): Arg1 -> Wrong function ('dl' for Dowload or 'ul' for Upload)");
		}
		if (function.equalsIgnoreCase("ul")) {
			if (drivePath != null) {// Si es Upload SI puede ser null el Arg2
				if (DriveUtils.extractDriveId(drivePath) == null) {
					valid = false;
					System.out.println("Error (Sintax): Arg2 -> Wrong drive path or Id.");
				}
			}
		} else { // Si es Dowload NO puede ser null el Arg2
			if (drivePath == null || DriveUtils.extractDriveId(drivePath) == null) {
				valid = false;
				System.out.println("Error (Sintax): Arg2 -> Wrong drive path or Id.");
			}
		}
		if (path == null || path.trim().isEmpty() || DriveUtils.extractDriveId(path) != null) {
			valid = false;
			System.out.println("Error (Sintax): Arg3 -> Wrong 'local' path.");
		}
		return valid;
	}
}
