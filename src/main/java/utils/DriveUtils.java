package utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import auth.DriveAuth;
import mimes.GoogleMimeTypes;

public class DriveUtils {

	private static Drive driveService = DriveAuth.getAuthDriveService();

	// Permite obtener sobre el path indicado el nombre del fichero o carpeta
	public static String generateParentPath(String id, String path) {
		File file = getDriveFile(id);
		String parentPath = path + "\\" + file.getName();
		return parentPath;
	}

	// Permite obtner un fichero de drive por Id
	public static File getDriveFile(String id) {
		File result = null;
		try {
			result = driveService.files().get(id).execute();
		} catch (IOException ioe) {
			System.out.println("An exception ocurred trying to GET a Drive File: " + ioe.getMessage());
		}
		return result;
	}

	// Permite obtener los ficheros por la Query indicada
	public static List<File> getDriveFiles(String queryTerm, String driveSpace, String filesFields) {
		FileList result = null;
		try {
			result = driveService.files().list().setQ(queryTerm).setSpaces(driveSpace).setFields(filesFields).execute();
		} catch (IOException ioe) {
			System.out.println("An exception ocurred trying to GET the Drive Files: " + ioe.getMessage());
		}
		return result.getFiles();
	}
	
	// Permite descargar un fichero de Drive en la ruta indicada
	public static void dowloadDriveFile(File file, String fullPath) {
		try (OutputStream fos = new FileOutputStream(fullPath)) {
			System.out.print("Downloading File ---> (" + file.getName() + ") ... ");
			driveService.files().get(file.getId()).executeMediaAndDownloadTo(fos);
			System.out.println("Completed!");
			fos.flush();
		} catch (IOException ioe) {
			System.out.println("An exception ocurred trying to DOWLOAD some Drive File: " + ioe.getMessage());
		}
	}

	// Permite crear una carpeta en Drive con nombre y padre indicados
	public static String createDriveFolder(String folderName, String parentId) {
		List<String> parent = new ArrayList<>();
		parent.add(parentId);
		File fileMetadata = new File();
		fileMetadata.setMimeType(GoogleMimeTypes.FOLDER.getMimeType());
		fileMetadata.setName(folderName);
		fileMetadata.setParents(parent);
		try {
			System.out.print("Creating Folder ---> (" + folderName + ") ... ");
			fileMetadata = driveService.files().create(fileMetadata).setFields("id").execute();
			System.out.println("Completed!");
		} catch (IOException ioe) {
			System.out.println("An exception ocurred trying to CREATE a Drive Folder: " + ioe.getMessage());
		}
		return fileMetadata.getId();
	}

	// Permite crear un fichero en Drive con nombre y padre indicados (Dejando que Drive seleccine su tipo)
	public static void createDriveFile(String fileName, String parentId, java.io.File file) {
		List<String> parent = new ArrayList<>();
		parent.add(parentId);
		File fileMetadata = new File();
		fileMetadata.setName(fileName);
		fileMetadata.setParents(parent);
		FileContent mediaContent = new FileContent(null, file);
		try {
			System.out.print("Uploading File ---> (" + file.getName() + ") ... ");
			driveService.files().create(fileMetadata, mediaContent).setFields("id").execute();
			System.out.println("Completed!");
		} catch (IOException ioe) {
			System.out.println("An exception ocurred trying to UPLOAD a Drive Folder: " + ioe.getMessage());
		}
	}

	// Permite extraer el Id Drive de una cadena (URL de navegador o URL de enlace compartido)
	public static String extractDriveId(String idOrUrl) {
		String id = null;
		Pattern pattern = Pattern.compile("^1.{32}$");
		Matcher m = null;
		idOrUrl = idOrUrl.replace("?usp=sharing", "");
		String[] splitValues = idOrUrl.split("/");
		for (String value : splitValues) {
			m = pattern.matcher(value);
			if (m.find()) {
				id = value;
			}
		}
		return id;
	}

	// Comprueba si un Id de Drive es una carpeta
	public static boolean isFolder(String id) {
		boolean isFolder = false;
		File file = getDriveFile(id);
		if (GoogleMimeTypes.FOLDER.getMimeType().equals(file.getMimeType())) {
			isFolder = true;
		}
		return isFolder;
	}
}
