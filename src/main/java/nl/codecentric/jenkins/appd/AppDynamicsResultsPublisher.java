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
import java.util.List;

import static nl.codecentric.jenkins.appd.util.LocalMessages.PUBLISHER_DISPLAYNAME;

/**
 * Main class for this Jenkins Plugin.
 * Hooks into the build flow as post-build step, then collecting data and generating the report.
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

    public List<DataCollectorDescriptor> getParserDescriptors() {
      return DataCollectorDescriptor.all();
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
      return true;
    }
  }

  /** Below fields are configured via the <code>config.jelly</code> page. */
  private String appdynamicsRestUri = "";
  private String applicationName = "";
  private Integer errorFailedThreshold = 0;
  private Integer errorUnstableThreshold = 0;

  @DataBoundConstructor
  public AppDynamicsResultsPublisher(final String appdynamicsRestUri, final String applicationName,
                                     int errorFailedThreshold, int errorUnstableThreshold) {
    setAppdynamicsRestUri(appdynamicsRestUri);
    setApplicationName(applicationName);
    setErrorFailedThreshold(errorFailedThreshold);
    setErrorUnstableThreshold(errorUnstableThreshold);
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

    logger.println("AppDynamics-Dashboard: No threshold configured for making the test "
        + Result.FAILURE.toString().toLowerCase());

    // add the report to the build object.
    AppDynamicsBuildAction a = new AppDynamicsBuildAction(build, logger);
    build.addAction(a);

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

  public int getErrorFailedThreshold() {
    return errorFailedThreshold;
  }

  public void setErrorFailedThreshold(int errorFailedThreshold) {
    this.errorFailedThreshold = Math.max(0, Math.min(errorFailedThreshold, 100));
  }

  public int getErrorUnstableThreshold() {
    return errorUnstableThreshold;
  }

  public void setErrorUnstableThreshold(int errorUnstableThreshold) {
    this.errorUnstableThreshold = Math.max(0, Math.min(errorUnstableThreshold,
        100));
  }
}
