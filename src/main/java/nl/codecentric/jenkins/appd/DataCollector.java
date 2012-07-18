package nl.codecentric.jenkins.appd;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.AbstractBuild;
import hudson.model.Describable;
import hudson.model.Hudson;
import hudson.model.TaskListener;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/** Basic DataCollector, provides some methods for instances. */
public abstract class DataCollector implements
    Describable<DataCollector>, ExtensionPoint {

  /** GLOB patterns that specify the performance report. */
  public final String glob;

  @DataBoundConstructor
  protected DataCollector(String glob) {
    this.glob = (glob == null || glob.length() == 0) ? getDefaultGlobPattern()
        : glob;
  }

  public DataCollectorDescriptor getDescriptor() {
    return (DataCollectorDescriptor) Hudson.getInstance().getDescriptorOrDie(
        getClass());
  }

  /** Parses the specified reports into {@link AppDynamicsReport}s. */
  public abstract Collection<AppDynamicsReport> parse(
      AbstractBuild<?, ?> build, Collection<File> reports, TaskListener listener)
      throws IOException;

  public abstract String getDefaultGlobPattern();

  /** All registered implementations. */
  public static ExtensionList<DataCollector> all() {
    return Hudson.getInstance().getExtensionList(DataCollector.class);
  }

}
