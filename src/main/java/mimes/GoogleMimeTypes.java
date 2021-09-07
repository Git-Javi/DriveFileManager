package mimes;

public enum GoogleMimeTypes {
	
	FOLDER("application/vnd.google-apps.folder");
	
	private String mimeType;
	
	private GoogleMimeTypes (String mimeType){
		this.mimeType = mimeType;
	}

	public String getMimeType() {
		return mimeType;
	}

}
