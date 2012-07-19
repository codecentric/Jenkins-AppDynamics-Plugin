package nl.codecentric.jenkins.appd;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;
import org.kohsuke.stapler.StaplerProxy;

import java.io.PrintStream;

/**
 * The build action is defined as {@link HealthReportingAction}, and will fail the build
 * should the response of the system get too low, or when other conditions are not met.
 */
public class AppDynamicsBuildAction implements HealthReportingAction, StaplerProxy {

  private final AbstractBuild<?, ?> build;
  private transient final PrintStream hudsonConsoleWriter;

  public AppDynamicsBuildAction(AbstractBuild<?, ?> pBuild, PrintStream logger) {
    build = pBuild;
    hudsonConsoleWriter = logger;
  }

  public String getIconFileName() {
    return null;
  }

  public String getDisplayName() {
    return null;
  }

  public String getUrlName() {
    return null;
  }

  public Object getTarget() {
    return null;
  }

  public HealthReport getBuildHealth() {
    return null;
  }
}
