package nl.codecentric.jenkins.appd;

import hudson.Extension;
import hudson.ExtensionPoint;
import hudson.model.*;
import nl.codecentric.jenkins.appd.rest.types.MetricData;
import nl.codecentric.jenkins.appd.rest.RestConnection;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * The {@link AppDynamicsDataCollector} will eventually fetch the performance statistics from the
 * AppDynamics REST interface and parse them into a {@link AppDynamicsReport}.<br />
 * <br />
 * Perhaps create separate Collectors again when this is more logical to create separate graphs. For
 * now this single collector should get all data.
 */
public class AppDynamicsDataCollector {

  private final RestConnection restConnection;
  private final AbstractBuild<?, ?> build;

  public AppDynamicsDataCollector(final RestConnection connection, final AbstractBuild<?, ?> build) {
    this.restConnection = connection;
    this.build = build;
  }

  /** Parses the specified reports into {@link AppDynamicsReport}s. */
  public AppDynamicsReport createReportFromMeasurements() {
    long buildStartTime = 1356877200000L;
    int durationInMinutes = 20;
    MetricData avgResponseTime = restConnection.fetchMetricData(
        "Overall Application Performance|Average Response Time (ms)", buildStartTime, durationInMinutes);

    // TODO implement
    return new AppDynamicsReport(avgResponseTime.getMetricValues());
  }

}
