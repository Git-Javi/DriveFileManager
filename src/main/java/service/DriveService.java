package service;

import java.util.List;
import com.google.api.services.drive.model.File;
import mimes.GoogleMimeTypes;
import utils.DriveUtils;
import utils.FileUtils;

public class DriveService {

	// Permite descargar el id de Drive indicado a la ruta seleccionada
	public static void filesAndDirectoryDownloader(String driveId, String path) {
		String parentPath = DriveUtils.generateParentPath(driveId, path);
		if (DriveUtils.isFolder(driveId) == false) {
			File file = DriveUtils.getDriveFile(driveId);
			DriveUtils.dowloadDriveFile(file, parentPath);
		}
		recursiveDirectoryDowloader(driveId, parentPath);
	}

	// Permite subir de la ruta seleccionada a drive añadiendo al parent indicado (O al principal en caso de omisión)
	public static void filesAndDirectoryUploader(String driveId, String path) {
		if (driveId != null) { // SI se ha indicado un id para UL (Se comprueba que sea una carpeta)
			if (DriveUtils.isFolder(driveId)) {
				recursiveDirectoryUploader(driveId, path);
			} else {
				System.out.println("Error : The Drive parent is not a folder!.");
			}
		} else { // NO se ha indicado un id para UL (Se subirá al principal)
			recursiveDirectoryUploader(driveId, path);
		}
	}

	// Permite descargar el contenido de un id de drive de manera recursiva
	private static void recursiveDirectoryDowloader(String parentId, String path) {

		final String queryTerm = "'" + parentId + "' in parents AND trashed = false";
		final String driveSpace = "drive";
		final String filesFields = "files(id,name,mimeType,parents)";

		List<File> files = DriveUtils.getDriveFiles(queryTerm, driveSpace, filesFields);
		FileUtils.createDirectory(path);

		for (File file : files) {
			String fullPath = path + "\\" + file.getName();
			if (GoogleMimeTypes.FOLDER.getMimeType().equals(file.getMimeType())) {
				recursiveDirectoryDowloader(file.getId(), fullPath);
			} else {
				DriveUtils.dowloadDriveFile(file, fullPath);
			}
		}
	}

	// Permite subir de la ruta seleccionada a drive de manera recursiva
	private static void recursiveDirectoryUploader(String parentId, String path) {
		java.io.File fileOrDir = new java.io.File(path);
		if (fileOrDir.isDirectory()) {
			String createdFolderId = DriveUtils.createDriveFolder(fileOrDir.getName(), parentId);
			for (java.io.File fod : fileOrDir.listFiles()) {
				if (fod.isFile()) {
					DriveUtils.createDriveFile(fod.getName(), createdFolderId, fod);
				} else if (fod.isDirectory()) {
					recursiveDirectoryUploader(createdFolderId, fod.getPath());
				}
			}
		} else if (fileOrDir.isFile()) {
			DriveUtils.createDriveFile(fileOrDir.getName(), parentId, fileOrDir);
		}
	}

}
