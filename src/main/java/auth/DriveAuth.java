package auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import app.App;

public class DriveAuth {

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	// Si se modifica el 'SCOPE', eliminar los tokens/carpeta previamente guardados.
	private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

	private static Properties properties = new Properties();

	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Carga las propiedades de la App
		properties.load(new FileInputStream(new File("src/main/resources/application.properties")));
		// Carga los credenciales
		InputStream is = App.class.getResourceAsStream(properties.getProperty("CREDENTIALS_FILE_PATH"));
		if (is == null) {
			throw new FileNotFoundException("Resource not found: " + properties.getProperty("CREDENTIALS_FILE_PATH"));
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(is));

		// Lanza la solicitud de autorización
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(properties.getProperty("TOKENS_DIRECTORY_PATH"))))
				.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	// Crea una autorización para el uso del servicio de Drive API como cliente
	public static Drive getAuthDriveService() {
		Drive driveService = null;
		try {
			final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
					.setApplicationName(properties.getProperty("APP_NAME")).build();
		} catch (GeneralSecurityException | IOException e) {
			System.out.println("An exception ocurred initializing the Drive Service: " + e.getMessage());
		}
		return driveService;
	}
}
