package nl.codecentric.jenkins.appd;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;
import nl.codecentric.jenkins.appd.rest.types.MetricData;
import nl.codecentric.jenkins.appd.rest.types.MetricValues;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static nl.codecentric.jenkins.appd.util.LocalMessages.PROJECTACTION_DISPLAYNAME;

/**
 * The {@link Action} that will be executed from your project and fetch the AppDynamics performance
 * data and display after a build.
 * The Project Action will show the graph for overall performance from all builds.
 */
public class AppDynamicsProjectAction implements Action {

  /** Logger. */
  private static final Logger LOGGER = Logger.getLogger(AppDynamicsProjectAction.class.getName());
  private static final long serialVersionUID = 1L;

  private static final String PLUGIN_NAME = "appdynamics-dashboard";

  public final AbstractProject<?, ?> project;
  private final String mainMetricKey;

  public AppDynamicsProjectAction(final AbstractProject project, final String mainMetricKey) {
    this.project = project;
    this.mainMetricKey = mainMetricKey;
    // TODO Add also list of all metrics fetched from AD, so we know which metrics to plot on
    // project page.
  }

  public String getDisplayName() {
    return PROJECTACTION_DISPLAYNAME.toString();
  }

  public String getUrlName() {
    return PLUGIN_NAME;
  }

  public String getIconFileName() {
    return "graph.gif";
  }

  public List<AppDynamicsReport> getExistingReportsList() {
    final List<AppDynamicsReport> adReportList = new ArrayList<AppDynamicsReport>();

    if (null == this.project) {
      return adReportList;
    }

    final List<? extends AbstractBuild<?, ?>> builds = project.getBuilds();
    for (AbstractBuild<?, ?> currentBuild : builds) {
      final AppDynamicsBuildAction performanceBuildAction = currentBuild.getAction(AppDynamicsBuildAction.class);
      if (performanceBuildAction == null) {
        continue;
      }
      final AppDynamicsReport report = performanceBuildAction.getBuildActionResultsDisplay().getAppDynamicsReport();
      if (report == null) {
        continue;
      }

      adReportList.add(report);
      // TODO Revert the order of reports, this is backwards now.
    }

    return adReportList;
  }

  public boolean isTrendVisibleOnProjectDashboard() {
    return getPerformanceReportList().size() >= 1;
  }

  /**
   * Graph of metric points over time.
   */
  public void doSummarizerGraphMainMetric(final StaplerRequest request,
                                final StaplerResponse response) throws IOException {
    final List<Double> averagesFromReports = getAveragesFromAllReports(getExistingReportsList(), mainMetricKey);

    final Graph graph = new GraphImpl(mainMetricKey + " Overall Graph") {

      protected DataSetBuilder<String, Integer> createDataSet() {
        DataSetBuilder<String, Integer> dataSetBuilder = new DataSetBuilder<String, Integer>();

        int i = 1;
        for (Double value : averagesFromReports) {
          // TODO Instead of numbers, make it possible to add build number, see:
//          ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(currentBuild);

          dataSetBuilder.add(value, mainMetricKey, i++);
        }

        return dataSetBuilder;
      }
    };

    graph.doPng(request, response);
  }


  private abstract class GraphImpl extends Graph {
    private final String graphTitle;

    protected GraphImpl(final String metricKey) {
      super(-1, 400, 300); // cannot use timestamp, since ranges may change
      this.graphTitle = stripTitle(metricKey);
    }

    private String stripTitle(final String metricKey) {
      return metricKey.substring(metricKey.lastIndexOf("|") + 1);
    }

    protected abstract DataSetBuilder<String, Integer> createDataSet();

    protected JFreeChart createGraph() {
      final CategoryDataset dataset = createDataSet().build();

      final JFreeChart chart = ChartFactory.createLineChart(graphTitle, // title
          "Build Number #", // category axis label
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

  private List<Double> getAveragesFromAllReports(final List<AppDynamicsReport> reports, final String metricKey) {
    List<Double> averages = new ArrayList<Double>();
    for (AppDynamicsReport report : reports) {
      double value = report.getAverageForMetric(metricKey);
      if (value >= 0) {
        averages.add(value);
      }
    }

    return averages;
  }






  public List<String> getPerformanceReportList() {
    List<String> testList = new ArrayList<String>();

    List<? extends AbstractBuild<?, ?>> builds = project.getBuilds();

    for (AbstractBuild<?, ?> currentBuild : builds) {
      ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(currentBuild);
      AppDynamicsBuildAction performanceBuildAction = currentBuild.getAction(AppDynamicsBuildAction.class);
      if (performanceBuildAction == null) {
        continue;
      }
      AppDynamicsReport report = performanceBuildAction.getBuildActionResultsDisplay().getAppDynamicsReport();
      if (report == null) {
        continue;
      }

      testList.add("report " + label.toString() + " errors: ");
    }

    return testList;
  }

}
