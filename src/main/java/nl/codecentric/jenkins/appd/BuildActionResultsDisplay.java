package nl.codecentric.jenkins.appd;

import hudson.model.AbstractBuild;
import hudson.model.ModelObject;
import hudson.model.TaskListener;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;
import nl.codecentric.jenkins.appd.rest.types.MetricData;
import nl.codecentric.jenkins.appd.rest.types.MetricValues;
import nl.codecentric.jenkins.appd.util.LocalMessages;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.awt.*;
import java.io.IOException;

/**
 * Root object of a AppDynamics Build Report.
 */
public class BuildActionResultsDisplay implements ModelObject {

  /**
   * The {@link AppDynamicsBuildAction} that this report belongs to.
   */
  private transient AppDynamicsBuildAction buildAction;
  private static AbstractBuild<?, ?> currentBuild = null;
  private AppDynamicsReport currentReport;

  /**
   * Parses the reports and build a {@link BuildActionResultsDisplay}.
   *
   * @throws java.io.IOException If a report fails to parse.
   */
  BuildActionResultsDisplay(final AppDynamicsBuildAction buildAction, TaskListener listener)
      throws IOException {
    this.buildAction = buildAction;

    currentReport = this.buildAction.getAppDynamicsReport();
    currentReport.setBuildAction(buildAction);
    addPreviousBuildReportToExistingReport();
  }

  public String getDisplayName() {
    return LocalMessages.REPORT_DISPLAYNAME.toString();
  }


  public AbstractBuild<?, ?> getBuild() {
    return buildAction.getBuild();
  }


  public AppDynamicsReport getAppDynamicsReport() {
    return currentReport;
  }

  private void addPreviousBuildReportToExistingReport() {
    // Avoid parsing all builds.
    if (BuildActionResultsDisplay.currentBuild == null) {
      BuildActionResultsDisplay.currentBuild = getBuild();
    } else {
      if (BuildActionResultsDisplay.currentBuild != getBuild()) {
        BuildActionResultsDisplay.currentBuild = null;
        return;
      }
    }

    AbstractBuild<?, ?> previousBuild = getBuild().getPreviousBuild();
    if (previousBuild == null) {
      return;
    }

    AppDynamicsBuildAction previousPerformanceAction = previousBuild.getAction(AppDynamicsBuildAction.class);
    if (previousPerformanceAction == null) {
      return;
    }

    BuildActionResultsDisplay previousBuildActionResults = previousPerformanceAction.getBuildActionResultsDisplay();
    if (previousBuildActionResults == null) {
      return;
    }

    AppDynamicsReport lastReport = previousBuildActionResults.getAppDynamicsReport();
    getAppDynamicsReport().setLastBuildReport(lastReport);
  }

  /**
   * Graph of metric points over time.
   */
  public void doSummarizerGraph(final StaplerRequest request,
                                final StaplerResponse response) throws IOException {
    final String metricKey = request.getParameter("metricDataKey");
    final MetricData metricData = this.currentReport.getMetricByKey(metricKey);

    final Graph graph = new GraphImpl(metricKey, metricData.getFrequency()) {

      protected DataSetBuilder<String, Integer> createDataSet() {
        DataSetBuilder<String, Integer> dataSetBuilder = new DataSetBuilder<String, Integer>();

        int i = 1;
        for (MetricValues value : metricData.getMetricValues()) {
          dataSetBuilder.add(value.getValue(), metricKey, i++);
        }

        return dataSetBuilder;
      }
    };

    graph.doPng(request, response);
  }


  private abstract class GraphImpl extends Graph {
    private final String graphTitle;
    private final String xLabel;

    protected GraphImpl(final String metricKey, final String frequency) {
      super(-1, 400, 300); // cannot use timestamp, since ranges may change
      this.graphTitle = stripTitle(metricKey);
      this.xLabel = "Time in " + frequency;
    }

    private String stripTitle(final String metricKey) {
      return metricKey.substring(metricKey.lastIndexOf("|") + 1);
    }

    protected abstract DataSetBuilder<String, Integer> createDataSet();

    protected JFreeChart createGraph() {
      final CategoryDataset dataset = createDataSet().build();

      final JFreeChart chart = ChartFactory.createLineChart(graphTitle, // title
          xLabel, // category axis label
          null, // value axis label
          dataset, // data
          PlotOrientation.VERTICAL, // orientation
          false, // include legend
          true, // tooltips
          false // urls
      );

      chart.setBackgroundPaint(Color.white);

      return chart;
    }
  }
}
