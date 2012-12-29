package nl.codecentric.jenkins.appd.rest;

/**
 * Class providing only the connection to the AppDynamics REST interface.
 * Checks all connection parameters and maintains the connection to the REST
 * interface.
 */
public class RestConnection {

  public RestConnection(final String restUri, final String username, final String password,
                        final String applicationName) {

  }

  public boolean validateConnection() {
    return true;
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

    // TODO later expand with more checks, such as 'http://' and end with '/rest/'
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
