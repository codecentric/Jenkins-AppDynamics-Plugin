package nl.codecentric.jenkins.appd;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

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


  public BuildStepMonitor getRequiredMonitorService() {
    return BuildStepMonitor.BUILD;
  }


}
