package nl.codecentric.jenkins.appd.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.httpclient.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class providing only the connection to the AppDynamics REST interface.
 * Checks all connection parameters and maintains the connection to the REST
 * interface.
 */
public class RestConnection {
  private static final Logger LOG = Logger.getLogger(RestConnection.class.getName());
  private final WebResource restResource;

  public RestConnection(final String restUri, final String username, final String password,
                        final String applicationName) {
    final String parsedUsername = parseUsername(username);
    final String parsedRestUri = parseRestUri(restUri);
    final String parsedApplicationName = parseApplicationName(applicationName);

    DefaultApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
    config.getState().setCredentials(null, null, -1, parsedUsername, password);
    Client restClient = ApacheHttpClient.create(config);
    restClient.setFollowRedirects(true);

    restResource = restClient.resource(parsedRestUri + parsedApplicationName);
  }

  public boolean validateConnection() {
    boolean validationResult = false;

    try {
      String response = restResource.path("business-transactions/").
          queryParam("output", "JSON").
          accept(MediaType.APPLICATION_JSON_TYPE).
          get(String.class);

      LOG.fine("Response from AppDynamics server ==> " + response);

      if (response != null && !response.isEmpty()) {
        validationResult = true;
      }
    } catch (Exception e) {
      LOG.log(Level.INFO, "Some problem connecting to the AppDynamics REST interface, see stack-trace for " +
          "more information", e);
      e.printStackTrace();
    }

    return validationResult;
  }


  public static boolean validateRestUri(final String restUri) {
    if (isFieldEmpty(restUri)) {
      return false;
    }

    if (restUri.startsWith("http://") || restUri.startsWith("https://")) {
      return true;
    } else {
      return false;
    }
  }

  public static boolean validateApplicationName(final String applicationName) {
    return !isFieldEmpty(applicationName);
  }

  public static boolean validateUsername(final String username) {
    return !isFieldEmpty(username);
  }

  public static boolean validatePassword(final String password) {
    return !isFieldEmpty(password);
  }

  private String parseUsername(final String username) {
    String parsedUsername = username;
    if (!username.contains("@")) {
      parsedUsername += "@customer1";
    }
    LOG.fine("Parsed username: " + parsedUsername);
    return parsedUsername;
  }

  private String parseRestUri(final String restUri) {
    StringBuilder parsedRestUri = new StringBuilder(parseRestSegment(restUri));

    String[] uriOrderedSegments = {"controller", "rest", "applications"};
    for (String segment : uriOrderedSegments) {
      if (!restUri.contains(segment)) {
        parsedRestUri.append(segment + "/");
      }
    }
    LOG.fine("Parsed REST uri: " + parsedRestUri.toString());
    return parsedRestUri.toString();
  }

  private String parseApplicationName(final String applicationName) {
    String parsedApplicationName;
    try {
      parsedApplicationName = URLEncoder.encode(applicationName, "UTF-8");
      // AppDynamics interface expects '%20' for spaces instead of '+'
      parsedApplicationName = parsedApplicationName.replaceAll("\\+", "%20");
    } catch (UnsupportedEncodingException e) {
      parsedApplicationName = applicationName;
    }
    LOG.fine("Parsed Application Name: " + parsedApplicationName);
    return parseRestSegment(parsedApplicationName);
  }

  private String parseRestSegment(final String restSegment) {
    String parsedSegment = restSegment;
    if (!restSegment.endsWith("/")) {
      parsedSegment += "/";
    }

    return parsedSegment;
  }

  private static boolean isFieldEmpty(final String field) {
    if (field == null || field.isEmpty()) {
      return true;
    }

    final String trimmedField = field.trim();
    if (trimmedField.length() == 0) {
      return true;
    }

    return false;
  }
}
