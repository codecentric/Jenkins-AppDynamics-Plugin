package nl.codecentric.jenkins.appd;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import org.kohsuke.stapler.DataBoundConstructor;

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

  @Extension
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

    public int getDefaultMeasurementInterval() {
        return 5;
    }

    public int getDefaultUnstableThreshold() {
        return 70;
    }

    public int getDefaultFailedThreshold() {
        return 90;
    }
  }

  public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
    /** Below fields are configured via the <code>config.jelly</code> page. */
  private String appdynamicsRestUri = "";
  private String applicationName = "";
  private Integer measurementInterval;
  private Integer errorFailedThreshold;
  private Integer errorUnstableThreshold;

  @DataBoundConstructor
  public AppDynamicsResultsPublisher(final String appdynamicsRestUri, final String applicationName,
                                     final Integer measurementInterval, final Integer errorFailedThreshold,
                                     final Integer errorUnstableThreshold) {
    setAppdynamicsRestUri(appdynamicsRestUri);
    setApplicationName(applicationName);
    setMeasurementInterval(measurementInterval);
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
    return BuildStepMonitor.BUILD;
  }


  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                         BuildListener listener) throws InterruptedException, IOException {
    PrintStream logger = listener.getLogger();

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

    // add the report to the build object.
    AppDynamicsDataCollector collector = new AppDynamicsDataCollector(this.appdynamicsRestUri, this.applicationName,
            this.measurementInterval, build.getDuration());
    AppDynamicsBuildAction a = new AppDynamicsBuildAction(build, logger, collector);
    build.addAction(a);

    // TODO Make sure the host is reachable, otherwise directly fail the build.

    // Kicking in the Data Collector
    AppDynamicsReport report = collector.parse(build, listener);

    // TODO Parse the reports and verify whether they were successful

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

  public void setAppdynamicsRestUri(String appdynamicsRestUri) {
    if (appdynamicsRestUri == null || appdynamicsRestUri.length() == 0) {
      throw new IllegalArgumentException("REST uri for AppDynamics Controller cannot be empty");
    }
    // TODO later expand with more checks, such as 'http://' and end with '/rest/'
    this.appdynamicsRestUri = appdynamicsRestUri;
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

  public Integer getMeasurementInterval() {
    return measurementInterval;
  }

  public void setMeasurementInterval(final Integer measurementInterval) {
    this.measurementInterval = Math.max(1, Math.min(measurementInterval, 10));
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
