package nl.codecentric.jenkins.appd;

import hudson.Extension;
import hudson.ExtensionPoint;
import hudson.model.*;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.net.URI;

/**
 * The {@link AppDynamicsDataCollector} will eventually fetch the performance statistics from the
 * AppDynamics REST interface and parse them into a {@link AppDynamicsReport}.<br />
 * <br />
 * Perhaps create separate Collectors again when this is more logical to create separate graphs. For
 * now this single collector should get all data.
 */
public class AppDynamicsDataCollector implements Describable<AppDynamicsDataCollector>, ExtensionPoint {

  @Extension
  public static class DataCollectorDescriptor extends Descriptor<AppDynamicsDataCollector> {
    @Override
    public String getDisplayName() {
      return "AppDynamics Data Collector";
    }

    /** Internal unique ID that distinguishes a parser. */
    @Override
    public final String getId() {
      return getClass().getName();
    }
  }

  private URI parsedRestUri;
  private String applicationName;

  @DataBoundConstructor
  public AppDynamicsDataCollector(final String appdynamicsRestUri, final String applicationName) {
    setParsedRestUri(appdynamicsRestUri);
    setApplicationName(applicationName);
  }

  public DataCollectorDescriptor getDescriptor() {
    return (DataCollectorDescriptor) Hudson.getInstance().getDescriptorOrDie(
        getClass());
  }

  /** Parses the specified reports into {@link AppDynamicsReport}s. */
  public AppDynamicsReport parse(AbstractBuild<?, ?> build, TaskListener listener) throws IOException {
    // TODO implement
    return new AppDynamicsReport();
  }

  public Boolean isRestHostReachable() {
    // TODO implement
    return true;
  }


  public String getParsedRestUri() {
    return parsedRestUri.toASCIIString();
  }

  public void setParsedRestUri(String appdynamicsRestUri) {
    if (appdynamicsRestUri == null || appdynamicsRestUri.length() == 0) {
      throw new IllegalArgumentException("REST uri for AppDynamics Controller cannot be empty");
    }
    // TODO later expand with more checks, such as 'http://' and end with '/rest/'
    this.parsedRestUri = URI.create(appdynamicsRestUri);
  }

  public String getApplicationName() {
    return applicationName;
  }

  public void setApplicationName(String applicationName) {
    if (applicationName == null || applicationName.length() == 0) {
      throw new IllegalArgumentException("Application Name cannot be empty");
    }
    this.applicationName = applicationName;
  }

}
