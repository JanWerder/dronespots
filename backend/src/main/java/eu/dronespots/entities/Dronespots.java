package eu.dronespots.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Dronespots {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long dronespotId;

	private String imagePath;
	private float longitude;
	private float latitude;
	private String description;
	private int footTraffic;
	private Boolean isAnonymous; 
	private Boolean isPublished;
	
	@OneToOne
	@JsonBackReference
	private Users createdBy;

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getFootTraffic() {
		return footTraffic;
	}

	public void setFootTraffic(int footTraffic) {
		this.footTraffic = footTraffic;
	}

	public long getDronespotId() {
		return dronespotId;
	}

	public void setDronespotId(long dronespotId) {
		this.dronespotId = dronespotId;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Users getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Users createdBy) {
		this.createdBy = createdBy;
	}

	public Boolean getIsPublished() {
		return isPublished;
	}

	public void setIsPublished(Boolean isPublished) {
		this.isPublished = isPublished;
	}

	public Boolean getIsAnonymous() {
		return isAnonymous;
	}

	public void setIsAnonymous(Boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}

}
