package eu.dronespots.rest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.imgscalr.Scalr;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.EnvironmentAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.core.env.Environment;

import eu.dronespots.entities.Dronespots;
import eu.dronespots.entities.Users;
import eu.dronespots.jsonmodels.DronespotModel;
import eu.dronespots.jsonmodels.OAuthModel;
import eu.dronespots.repositories.DronespotRepository;
import eu.dronespots.util.GenericSemaphore;

@SuppressWarnings("deprecation")
@RestController
@RequestMapping("/api")
public class DronespotReceiver  implements EnvironmentAware{

	Logger logger = LoggerFactory.getLogger(DronespotReceiver.class);

	@Autowired
	EntityManager em;

	@Autowired
	DronespotRepository dronespot;

	@Autowired
	GenericSemaphore sema;

	@Autowired
	Environment env;

	@Override
    public void setEnvironment(final Environment environment) {
        this.env = environment;
    }

	private String uri = "https://accounts.google.com/o/oauth2/token";
	private String client_id = "";
	private String client_secret = "";

	public String client_id() {
        return env.getProperty("custom.oauth.clientId");
    }

	public String client_secret() {
        return env.getProperty("custom.oauth.clientSecret");
    }

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/dronespot", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	@Transactional
	@ResponseBody
	public ResponseEntity newDronespot(@RequestHeader String idToken, @RequestPart("formBody") String formBody,
			@RequestPart("imageBlob") MultipartFile imageBlob) throws IOException, GeneralSecurityException {
		// @RequestBody NewDronespot newDronespot
		logger.info("Received new Dronespot");

		GoogleIdToken verifiedIdToken = verifyIdToken(idToken);
		if (verifiedIdToken == null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		Dronespots newDronespot = new ObjectMapper().readValue(formBody, Dronespots.class);

		BufferedImage srcImage = ImageIO.read(new ByteArrayInputStream(imageBlob.getBytes()));
		if (srcImage == null) {
			return new ResponseEntity<>(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
		}

		Dronespots dronespot = new Dronespots();
		dronespot.setDescription(newDronespot.getDescription());
		dronespot.setFootTraffic(newDronespot.getFootTraffic());
		dronespot.setLatitude(newDronespot.getLatitude());
		dronespot.setLongitude(newDronespot.getLongitude());
		dronespot.setIsAnonymous(newDronespot.getIsAnonymous());
		dronespot.setIsPublished(false);

		if (!this.checkForDronespotSimilarity(dronespot.getLatitude(), dronespot.getLongitude())) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}

		if (idToken != null) {
			Payload payload = verifiedIdToken.getPayload();
			Users user = (Users) em.createQuery("select u from Users u where email = :email")
					.setParameter("email", payload.getEmail()).getResultList().get(0);
			dronespot.setCreatedBy(user);
		}

		em.persist(dronespot);
		em.flush();

		ApplicationHome home = new ApplicationHome(this.getClass());
		String relPath = "/img/" + dronespot.getDronespotId() + ".jpg";

		srcImage = this.resizeImage(srcImage);

		File destination = new File(home.getDir() + relPath);
		ImageIO.write(srcImage, "jpg", destination);
		dronespot.setImagePath(relPath);

		em.persist(dronespot);

		return new ResponseEntity(HttpStatus.OK);
	}

	private BufferedImage resizeImage(BufferedImage srcImage) {
		int targetWidth = 4000;
		int targetHeight = 2250;

		if (srcImage.getWidth() >= targetWidth || srcImage.getHeight() >= targetHeight) {
			return Scalr.resize(srcImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, targetWidth,
					targetHeight, Scalr.OP_ANTIALIAS);
		} else {
			return srcImage;
		}
	}

	private boolean checkForDronespotSimilarity(float latitude, float longitude) {
		// Build a bounding box (+/- 0.001)
		float offset = 0.001f;
		float nelng = longitude + offset;
		float nelat = latitude + offset;
		float swlng = longitude - offset;
		float swlat = latitude - offset;

		Query q = em.createQuery(
				"select d from Dronespots d WHERE longitude <= :nelng AND longitude >= :swlng AND latitude <= :nelat AND latitude >= :swlat");
		q.setParameter("nelng", nelng);
		q.setParameter("nelat", nelat);
		q.setParameter("swlng", swlng);
		q.setParameter("swlat", swlat);
		return q.getResultList().isEmpty();
	}

	@GetMapping("/api/mapaccesstoken")
	@Transactional
	public ResponseEntity getMapAccessToken() {

		String accessToken = null;
		accessToken = this.env.getProperty("custom.mapbox.accessToken");

		if (accessToken == null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		return new ResponseEntity<>(accessToken, HttpStatus.OK);
	}

	@GetMapping("/markers/{nelng}/{nelat}/{swlng}/{swlat}")
	@Transactional
	public ResponseEntity<DronespotModel[]> loadMarkers(@PathVariable float nelng, @PathVariable float nelat,
			@PathVariable float swlng, @PathVariable float swlat) {

		Query q = em.createQuery(
				"select d from Dronespots d WHERE isPublished = true AND longitude <= :nelng AND longitude >= :swlng AND latitude <= :nelat AND latitude >= :swlat");
		q.setParameter("nelng", nelng);
		q.setParameter("nelat", nelat);
		q.setParameter("swlng", swlng);
		q.setParameter("swlat", swlat);
		List<Dronespots> dronespotList = q.getResultList();

		ArrayList<DronespotModel> dronespotResponseList = new ArrayList<DronespotModel>();

		for (Dronespots dronespot : dronespotList) {

			DronespotModel dronespotOutputModel = new DronespotModel();
			dronespotOutputModel.setDronespotId(dronespot.getDronespotId());
			dronespotOutputModel.setImagePath(dronespot.getImagePath());
			dronespotOutputModel.setDescription(dronespot.getDescription());
			dronespotOutputModel.setFootTraffic(dronespot.getFootTraffic());
			dronespotOutputModel.setLatitude(dronespot.getLatitude());
			dronespotOutputModel.setLongitude(dronespot.getLongitude());

			Boolean isAnon = dronespot.getIsAnonymous();
			if (isAnon.equals(false)) {
				dronespotOutputModel.setAuthorDisplayName(dronespot.getCreatedBy().getDisplayname());
			}

			dronespotResponseList.add(dronespotOutputModel);
		}

		return new ResponseEntity<>(dronespotResponseList.toArray(new DronespotModel[0]), HttpStatus.OK);
	}

	@PostMapping("/auth/google")
	@Transactional
	public ResponseEntity<OAuthModel> getAccessToken(@RequestBody String code)
			throws JSONException, GeneralSecurityException, IOException {
		OAuthModel oAuthModel = new OAuthModel();

		// Change the one-time-ticket for a proper access token
		RestTemplate restTemplate = new RestTemplate();

		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.add("client_id", client_id());
		map.add("client_secret", client_secret());
		map.add("redirect_uri", "postmessage");
		map.add("grant_type", "authorization_code");
		map.add("code", java.net.URLDecoder.decode(code, StandardCharsets.UTF_8.name()));

		String result = restTemplate.postForObject(uri, map, String.class);

		JSONObject obj = new JSONObject(result);
		oAuthModel.setIdToken(obj.getString("id_token"));

		// Verify the JWT and get the actual user data
		GoogleIdToken idToken = verifyIdToken(oAuthModel.getIdToken());
		if (idToken != null) {
			Payload payload = idToken.getPayload();
			oAuthModel.setEmail(payload.getEmail());
			oAuthModel.setUserId(payload.getSubject());
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		// Check if the User is already registered
		Query q = em.createQuery("Select u from Users u WHERE email = :email");
		q.setParameter("email", oAuthModel.getEmail());
		if (!q.getResultList().isEmpty()) {
			oAuthModel.setIsRegistered(true);
			oAuthModel.setDisplayname(((Users) q.getResultList().get(0)).getDisplayname());
		} else {
			oAuthModel.setIsRegistered(false);
		}

		return new ResponseEntity<>(oAuthModel, HttpStatus.OK);

	}

	private GoogleIdToken verifyIdToken(String idToken) throws GeneralSecurityException, IOException {
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
				.setAudience(Collections.singletonList(client_id())).build();

		GoogleIdToken verifiedIdToken = verifier.verify(idToken);
		return verifiedIdToken;
	}

	@PutMapping("/auth/register")
	@Transactional
	@ResponseBody
	public ResponseEntity register(@RequestHeader String idToken, @RequestBody String formBody)
			throws GeneralSecurityException, IOException {

		GoogleIdToken verifiedIdToken = verifyIdToken(idToken);
		if (verifiedIdToken == null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		OAuthModel mappedUser = new ObjectMapper().readValue(formBody, OAuthModel.class);

		Query q = em.createQuery("Select u from Users u WHERE email = :email");
		q.setParameter("email", mappedUser.getEmail());
		if (q.getResultList().isEmpty()) {
			Users user = new Users();
			user.setDisplayname(mappedUser.getDisplayname());
			user.setEmail(mappedUser.getEmail());
			user.setOauthid(mappedUser.getUserId());
			em.persist(user);
			em.flush();
		} else {
			return new ResponseEntity(HttpStatus.CONFLICT);
		}

		return new ResponseEntity(HttpStatus.OK);

	}

}
