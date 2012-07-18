package nl.codecentric.jenkins.appd;

import hudson.model.AbstractProject;
import hudson.model.Action;
import nl.codecentric.jenkins.appd.util.LocalMessages;

import java.util.logging.Logger;

/**
 * The {@link Action} that will be executed from your project and fetch the AppDynamics performance
 * data and display after a build.
 */
public class AppDynamicsProjectAction implements Action {

  /** Logger. */
  private static final Logger LOGGER = Logger.getLogger(AppDynamicsProjectAction.class.getName());
  private static final long serialVersionUID = 1L;

  private static final String PLUGIN_NAME = "appdynamics-dashboard";

  public final AbstractProject<?, ?> project;

  public AppDynamicsProjectAction(AbstractProject project) {
    this.project = project;
  }

  public String getDisplayName() {
    return LocalMessages.getProjectActionDisplayName();
  }

  public String getUrlName() {
    return PLUGIN_NAME;
  }

  public String getIconFileName() {
    return "graph.gif";
  }

}
