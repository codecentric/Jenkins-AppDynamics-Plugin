package nl.codecentric.jenkins.appd;

import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;

/**
 * Main class for this Jenkins Plugin.
 * Hooks into the build flow as post-build step, then collecting data and generating the report.
 */
public class AppDynamicsResultsPublisher extends Recorder {


  public BuildStepMonitor getRequiredMonitorService() {
    return null;
  }
}
