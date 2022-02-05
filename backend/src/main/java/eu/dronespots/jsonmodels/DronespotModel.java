package eu.dronespots.jsonmodels;

public class DronespotModel {

	private long dronespotId;

	private String imagePath;
	private float latitude;
	private float longitude;	
	private String description;
	private int footTraffic;
	private String authorDisplayName;


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

	public String getAuthorDisplayName() {
		return authorDisplayName;
	}

	public void setAuthorDisplayName(String authorDisplayName) {
		this.authorDisplayName = authorDisplayName;
	}

}
