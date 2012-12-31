package nl.codecentric.jenkins.appd;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import nl.codecentric.jenkins.appd.rest.RestConnection;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintStream;

import static nl.codecentric.jenkins.appd.util.LocalMessages.PUBLISHER_DISPLAYNAME;

/**
 * Main class for this Jenkins Plugin.<br />
 * Hooks into the build flow as post-build step, then collecting data and generating the report.<br /><br />
 * <p/>
 * Configuration is set from the Jenkins Build Configuration menu. When a build is triggered, the
 * {@link AppDynamicsResultsPublisher#perform(hudson.model.AbstractBuild, hudson.Launcher, hudson.model.BuildListener)}
 * method is called. This will then trigger the {@link AppDynamicsDataCollector} and parse any results and produces
 * {@link AppDynamicsReport}'s.<br />
 * A {@link AppDynamicsBuildAction} is used to store data per-build, so it can be compared later.
 */
public class AppDynamicsResultsPublisher extends Recorder {

  private static final String DEFAULT_USERNAME = "username@customer1";
  private static final int DEFAULT_THRESHOLD_UNSTABLE = 70;
  private static final int DEFAULT_THRESHOLD_FAILED = 90;

  public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

    @Override
    public String getDisplayName() {
      return PUBLISHER_DISPLAYNAME.toString();
    }

    @Override
    public String getHelpFile() {
      return "/plugin/appdynamics-dashboard/help.html";
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
      return true;
    }

    public String getDefaultUsername() {
      return DEFAULT_USERNAME;
    }

    public int getDefaultUnstableThreshold() {
        return DEFAULT_THRESHOLD_UNSTABLE;
    }

    public int getDefaultFailedThreshold() {
        return DEFAULT_THRESHOLD_FAILED;
    }

    public FormValidation doCheckAppdynamicsRestUri(@QueryParameter final String appdynamicsRestUri) {
      FormValidation validationResult;

      if (RestConnection.validateRestUri(appdynamicsRestUri)) {
        validationResult = FormValidation.ok();
      } else {
        validationResult = FormValidation.error("AppDynamics REST uri is not valid, cannot be empty and has to " +
            "start with 'http://' or 'https://'");
      }

      return validationResult;
    }

    public FormValidation doCheckUsername(@QueryParameter final String username) {
      FormValidation validationResult;

      if (RestConnection.validateUsername(username)) {
        validationResult = FormValidation.ok();
      } else {
        validationResult = FormValidation.error("Username for REST interface cannot be empty");
      }

      return validationResult;
    }

    public FormValidation doCheckPassword(@QueryParameter final String password) {
      FormValidation validationResult;

      if (RestConnection.validatePassword(password)) {
        validationResult = FormValidation.ok();
      } else {
        validationResult = FormValidation.error("Password for REST interface cannot be empty");
      }

      return validationResult;
    }

    public FormValidation doCheckApplicationName(@QueryParameter final String applicationName) {
      FormValidation validationResult;

      if (RestConnection.validateApplicationName(applicationName)) {
        validationResult = FormValidation.ok();
      } else {
        validationResult = FormValidation.error("AppDynamics application name cannot be empty");
      }

      return validationResult;
    }

    public FormValidation doTestAppDynamicsConnection(@QueryParameter("appdynamicsRestUri") final String appdynamicsRestUri,
                                                      @QueryParameter("username") final String username,
                                                      @QueryParameter("password") final String password,
                                                      @QueryParameter("applicationName") final String applicationName) {
      FormValidation validationResult;
      RestConnection connection = new RestConnection(appdynamicsRestUri, username, password, applicationName);

      if (connection.validateConnection()) {
        validationResult = FormValidation.ok("Connection successful");
      } else {
        validationResult = FormValidation.warning("Connection with AppDynamics RESTful interface could not be established");
      }

      return validationResult;
    }
  }

  @Extension
  public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

  private RestConnection connection;
  /** Below fields are configured via the <code>config.jelly</code> page. */
  private String appdynamicsRestUri = "";
  private String username = "";
  private String password = "";
  private String applicationName = "";
  private Integer errorFailedThreshold;
  private Integer errorUnstableThreshold;

  @DataBoundConstructor
  public AppDynamicsResultsPublisher(final String appdynamicsRestUri, final String username,
                                     final String password, final String applicationName,
                                     final Integer errorFailedThreshold, final Integer errorUnstableThreshold) {
    setAppdynamicsRestUri(appdynamicsRestUri);
    setUsername(username);
    setPassword(password);
    setApplicationName(applicationName);
    setErrorFailedThreshold(errorFailedThreshold);
    setErrorUnstableThreshold(errorUnstableThreshold);
  }

  @Override
  public BuildStepDescriptor<Publisher> getDescriptor() {
      return DESCRIPTOR;
  }

  @Override
  public Action getProjectAction(AbstractProject<?, ?> project) {
    return new AppDynamicsProjectAction(project);
  }

  public BuildStepMonitor getRequiredMonitorService() {
    // No synchronization necessary between builds
    return BuildStepMonitor.NONE;
  }


  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
      throws InterruptedException, IOException {
    PrintStream logger = listener.getLogger();

    RestConnection connection = new RestConnection(appdynamicsRestUri, username, password, applicationName);
    logger.println("Verify connection to AppDynamics REST interface ...");
    if (!connection.validateConnection()) {
      logger.println("Connection to AppDynamics REST interface unsuccessful, cannot proceed with this build step");
      build.setResult(Result.FAILURE);
      return true;
    }

    logger.println("Connection successful, continue to fetch measurements from AppDynamics Controller ...");

    // add the report to the build object.
    AppDynamicsDataCollector dataCollector = new AppDynamicsDataCollector(connection, build);
    AppDynamicsBuildAction buildAction = new AppDynamicsBuildAction(build, logger, dataCollector);
    build.addAction(buildAction);


    // Kicking in the Data Collector
    AppDynamicsReport report = dataCollector.createReportFromMeasurements();

    // TODO Parse the reports and verify whether they were successful



    if (errorUnstableThreshold >= 0 && errorUnstableThreshold <= 100) {
      logger.println("Performance: Percentage of errors greater or equal than "
          + errorUnstableThreshold + "% sets the build as "
          + Result.UNSTABLE.toString().toLowerCase());
    } else {
      logger.println("Performance: No threshold configured for making the test "
          + Result.UNSTABLE.toString().toLowerCase());
    }
    if (errorFailedThreshold >= 0 && errorFailedThreshold <= 100) {
      logger.println("Performance: Percentage of errors greater or equal than "
          + errorFailedThreshold + "% sets the build as "
          + Result.FAILURE.toString().toLowerCase());
    } else {
      logger.println("Performance: No threshold configured for making the test "
          + Result.FAILURE.toString().toLowerCase());
    }


    // mark the build as unstable or failure depending on the outcome.
    double thresholdTolerance = 0.00000001;

    double errorPercent = 0.0; //r.errorPercent();
    Result result = Result.SUCCESS;
    if (errorFailedThreshold >= 0 && errorPercent - errorFailedThreshold > thresholdTolerance) {
      result = Result.FAILURE;
      build.setResult(Result.FAILURE);
    } else if (errorUnstableThreshold >= 0
        && errorPercent - errorUnstableThreshold > thresholdTolerance) {
      result = Result.UNSTABLE;
    }
    if (result.isWorseThan(build.getResult())) {
      build.setResult(result);
    }
//      logger.println("Performance: File " + r.getReportFileName()
//          + " reported " + errorPercent
//          + "% of errors [" + result + "]. Build status is: "
//          + build.getResult());


    return true;
  }

  public String getAppdynamicsRestUri() {
    return appdynamicsRestUri;
  }

  public void setAppdynamicsRestUri(final String appdynamicsRestUri) {
    this.appdynamicsRestUri = appdynamicsRestUri;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public String getApplicationName() {
    return applicationName;
  }

  public void setApplicationName(final String applicationName) {
    this.applicationName = applicationName;
  }

  public Integer getErrorFailedThreshold() {
    return errorFailedThreshold;
  }

  public void setErrorFailedThreshold(final Integer errorFailedThreshold) {
    this.errorFailedThreshold = Math.max(0, Math.min(errorFailedThreshold, 100));
  }

  public Integer getErrorUnstableThreshold() {
    return errorUnstableThreshold;
  }

  public void setErrorUnstableThreshold(final Integer errorUnstableThreshold) {
    this.errorUnstableThreshold = Math.max(0, Math.min(errorUnstableThreshold, 100));
  }
}
