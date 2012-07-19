package nl.codecentric.jenkins.appd;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * The {@link AppDynamicsDataCollector} will eventually fetch the performance statistics from the
 * AppDynamics REST interface and parse them into a {@link AppDynamicsReport}.
 */
public class AppDynamicsDataCollector extends DataCollector {

  @Extension
  public static class DescriptorImpl extends DataCollectorDescriptor {
    @Override
    public String getDisplayName() {
      return "AppDynamics";
    }
  }

  @DataBoundConstructor
  public AppDynamicsDataCollector(String glob) {
    super(glob);
  }

  @Override
  public String getDefaultGlobPattern() {
    return "**/*.jtl";
  }

  @Override
  public Collection<AppDynamicsReport> parse(AbstractBuild<?, ?> build,
                                             Collection<File> reports, TaskListener listener) throws IOException {
    return null;
  }
}