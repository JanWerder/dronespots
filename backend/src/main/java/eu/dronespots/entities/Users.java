package eu.dronespots.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long userId;

	private String displayname;
	private String email;
	private String oauthid;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getOauthid() {
		return oauthid;
	}
	public void setOauthid(String oauthid) {
		this.oauthid = oauthid;
	}
	public String getDisplayname() {
		return displayname;
	}
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}


}
