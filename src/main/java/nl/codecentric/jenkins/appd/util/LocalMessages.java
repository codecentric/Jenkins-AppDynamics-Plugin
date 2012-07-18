package nl.codecentric.jenkins.appd.util;

import java.util.ResourceBundle;

/**
 * Class provides access to the Messages {@link ResourceBundle} and hides initialization of the
 * properties file.
 */
public class LocalMessages {

  private final static ResourceBundle messages = ResourceBundle.getBundle("Messages");

  public static String getProjectActionDisplayName() {
    return messages.getString("AppDynamicsProjectAction.DisplayName");
  }

  public static String getPublisherDisplayName() {
    return messages.getString("AppDynamicsResultsPublisher.DisplayName");
  }

}
