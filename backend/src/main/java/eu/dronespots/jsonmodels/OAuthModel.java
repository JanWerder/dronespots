package eu.dronespots.jsonmodels;

public class OAuthModel {

	private String idToken;
	private String email;
	private String userId;
	private Boolean isRegistered;
	private String displayname;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Boolean getIsRegistered() {
		return isRegistered;
	}

	public void setIsRegistered(Boolean isRegistered) {
		this.isRegistered = isRegistered;
	}

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public String getIdToken() {
		return idToken;
	}

	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}

}
