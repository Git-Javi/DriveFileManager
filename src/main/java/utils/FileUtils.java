package utils;

public class FileUtils {

	// Permite crear un directorio si no existe
	public static void createDirectory(String path) {
		java.io.File dir = new java.io.File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
	
}
