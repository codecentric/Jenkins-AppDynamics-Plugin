package nl.codecentric.jenkins.appd;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.util.StreamTaskListener;
import nl.codecentric.jenkins.appd.util.LocalMessages;
import org.kohsuke.stapler.StaplerProxy;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The build action is defined as {@link Action}, and will fail the build
 * should the response of the system get too low, or when other conditions are not met.<br />
 * <br />
 * The {@link AppDynamicsBuildAction} relays output and displaying of the build output results
 * to the {@link BuildActionResultsDisplay}.
 */
public class AppDynamicsBuildAction implements Action, StaplerProxy {

  private final AbstractBuild<?, ?> build;
  private final AppDynamicsDataCollector collector;
  private transient final PrintStream hudsonConsoleWriter;
  private transient WeakReference<BuildActionResultsDisplay> buildActionResultsDisplay;

  private transient static final Logger logger = Logger.getLogger(AppDynamicsBuildAction.class.getName());

  public AppDynamicsBuildAction(AbstractBuild<?, ?> pBuild, PrintStream logger, AppDynamicsDataCollector pCollector) {
    build = pBuild;
    hudsonConsoleWriter = logger;
    collector = pCollector;
  }

  public String getIconFileName() {
    return "graph.gif";
  }

  public String getDisplayName() {
    return LocalMessages.BUILDACTION_DISPLAYNAME.toString();
  }

  public String getUrlName() {
    return "appdynamics-dashboard";
  }

  public AbstractBuild<?, ?> getBuild() {
    return build;
  }

  public AppDynamicsDataCollector getCollector() {
    return collector;
  }

  public BuildActionResultsDisplay getTarget() {
    return getBuildActionResultsDisplay();
  }

  public BuildActionResultsDisplay getBuildActionResultsDisplay() {
    BuildActionResultsDisplay buildDisplay = null;
    WeakReference<BuildActionResultsDisplay> wr = this.buildActionResultsDisplay;
    if (wr != null) {
      buildDisplay = wr.get();
      if (buildDisplay != null)
        return buildDisplay;
    }

    try {
      buildDisplay = new BuildActionResultsDisplay(this, StreamTaskListener.fromStdout());
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error creating new BuildActionResultsDisplay()", e);
    }
    this.buildActionResultsDisplay = new WeakReference<BuildActionResultsDisplay>(buildDisplay);
    return buildDisplay;
  }

  public void setBuildActionResultsDisplay(WeakReference<BuildActionResultsDisplay> buildActionResultsDisplay) {
    this.buildActionResultsDisplay = buildActionResultsDisplay;
  }
}
