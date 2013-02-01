package nl.codecentric.jenkins.appd;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

import static nl.codecentric.jenkins.appd.util.LocalMessages.PROJECTACTION_DISPLAYNAME;

/**
 * The {@link Action} that will be executed from your project and fetch the AppDynamics performance
 * data and display after a build.
 * The Project Action will show the graph for overall performance from all builds.
 */
public class AppDynamicsProjectAction implements Action {

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(AppDynamicsProjectAction.class.getName());
  private static final long serialVersionUID = 1L;

  private static final String PLUGIN_NAME = "appdynamics-dashboard";

  private final AbstractProject<?, ?> project;
  private final String mainMetricKey;
  private final String[] allMetricKeys;

  public AppDynamicsProjectAction(final AbstractProject project, final String mainMetricKey,
                                  final String[] allMetricKeys) {
    this.project = project;
    this.mainMetricKey = mainMetricKey;
    this.allMetricKeys = allMetricKeys;
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

  /**
   * Method necessary to get the side-panel included in the Jelly file
   * @return this {@link AbstractProject}
   */
  public AbstractProject<?, ?> getProject() {
    return this.project;
  }

  public boolean isTrendVisibleOnProjectDashboard() {
    return getExistingReportsList().size() >= 1;
  }

  public List<String> getAvailableMetricKeys() {
    return Arrays.asList(allMetricKeys);
  }

  /**
   * Graph of metric points over time.
   */
  public void doSummarizerGraphMainMetric(final StaplerRequest request,
                                          final StaplerResponse response) throws IOException {
    final Map<ChartUtil.NumberOnlyBuildLabel, Double> averagesFromReports =
        getAveragesFromAllReports(getExistingReportsList(), mainMetricKey);

    final Graph graph = new GraphImpl(mainMetricKey + " Overall Graph") {

      protected DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> createDataSet() {
        DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dataSetBuilder =
            new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        for (ChartUtil.NumberOnlyBuildLabel label : averagesFromReports.keySet()) {
          dataSetBuilder.add(averagesFromReports.get(label), mainMetricKey, label);
        }

        return dataSetBuilder;
      }
    };

    graph.doPng(request, response);
  }

  /**
   * Graph of metric points over time, metric to plot set as request parameter.
   */
  public void doSummarizerGraphForMetric(final StaplerRequest request,
                                          final StaplerResponse response) throws IOException {
    final String metricKey = request.getParameter("metricDataKey");
    final Map<ChartUtil.NumberOnlyBuildLabel, Double> averagesFromReports =
        getAveragesFromAllReports(getExistingReportsList(), metricKey);

    final Graph graph = new GraphImpl(metricKey + " Overall Graph") {

      protected DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> createDataSet() {
        DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dataSetBuilder =
            new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        for (ChartUtil.NumberOnlyBuildLabel label : averagesFromReports.keySet()) {
          dataSetBuilder.add(averagesFromReports.get(label), metricKey, label);
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

    protected abstract DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> createDataSet();

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

  private List<AppDynamicsReport> getExistingReportsList() {
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
    }

    return adReportList;
  }

  private Map<ChartUtil.NumberOnlyBuildLabel, Double> getAveragesFromAllReports(
      final List<AppDynamicsReport> reports, final String metricKey) {
    Map<ChartUtil.NumberOnlyBuildLabel, Double> averages = new TreeMap<ChartUtil.NumberOnlyBuildLabel, Double>();
    for (AppDynamicsReport report : reports) {
      double value = report.getAverageForMetric(metricKey);
      if (value >= 0) {
        ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(report.getBuild());
        averages.put(label, value);
      }
    }

    return averages;
  }
}
