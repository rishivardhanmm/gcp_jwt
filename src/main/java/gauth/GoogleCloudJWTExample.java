package gauth;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import io.jsonwebtoken.Jwts;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;

public class GoogleCloudJWTExample {

    public static void main(String[] args) throws IOException {
        Properties config = loadConfig();
    	String serviceAccountKeyPath = config.getProperty("serviceAccountKeyPath");
        InputStream keyFileStream = new FileInputStream(serviceAccountKeyPath);
        GoogleCredentials credentials = loadCredentials(keyFileStream);
        long refreshIntervalInMillis = 5000;
        while (true) {
            long expirationTimeInMillis = System.currentTimeMillis() + 5000;
            String jwt = createJWT(credentials, expirationTimeInMillis);
            System.out.println("JWT: " + jwt);
            System.out.println("JWT Decoded: " + decodeJWT(jwt));
            try {
                Thread.sleep(refreshIntervalInMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static GoogleCredentials loadCredentials(InputStream keyFileStream) {
        try {
            return GoogleCredentials.fromStream(keyFileStream);
        } catch (IOException e) {
            throw new RuntimeException("Error loading credentials", e);
        }
    }

    private static String createJWT(GoogleCredentials credentials, long expirationTimeInMillis) {
        if (credentials instanceof ServiceAccountCredentials) {
            ServiceAccountCredentials serviceAccountCredentials = (ServiceAccountCredentials) credentials;
            String clientEmail = serviceAccountCredentials.getClientEmail();
            String clientId = serviceAccountCredentials.getClientId();

            return Jwts.builder()
			        .setIssuer(clientEmail)
                    .setAudience(clientId)
			        .setSubject(clientEmail)
			        .setIssuedAt(new Date())
			        .setExpiration(new Date(expirationTimeInMillis))
			        .compact();
        } else {
            throw new IllegalArgumentException("Credentials must be a ServiceAccountCredentials.");
        }
    }
	
    private static Properties loadConfig() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading configuration", e);
        }
        return properties;
    }

    private static String decodeJWT(String jwt) {
        String[] chunks = jwt.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        return payload;
    }
}
