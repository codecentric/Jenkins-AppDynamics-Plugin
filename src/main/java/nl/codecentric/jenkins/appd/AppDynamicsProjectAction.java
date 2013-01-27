package nl.codecentric.jenkins.appd;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

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

  public AppDynamicsProjectAction(AbstractProject project) {
    this.project = project;
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

  public List<String> getPerformanceReportList() {
    List<String> testList = new ArrayList<String>();

    DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dataSet = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();
    List<? extends AbstractBuild<?, ?>> builds = project.getBuilds();

    int nbBuildsToAnalyze = builds.size();
    for (AbstractBuild<?, ?> currentBuild : builds) {
      ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(currentBuild);
      AppDynamicsBuildAction performanceBuildAction = currentBuild.getAction(AppDynamicsBuildAction.class);
      if (performanceBuildAction == null) {
        continue;
      }
      AppDynamicsReport report = null;
      report = performanceBuildAction.getBuildActionResultsDisplay().getAppDynamicsReport();
      if (report == null) {
        nbBuildsToAnalyze--;
        continue;
      }

      testList.add("report " + label.toString() + " errors: ");
    }

    return testList;
  }


  private DataSetBuilder getTrendReportData(final StaplerRequest request,
                                            String performanceReportNameFile) {

    DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dataSet = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();
    List<? extends AbstractBuild<?, ?>> builds = project.getBuilds();

    int nbBuildsToAnalyze = builds.size();
    for (AbstractBuild<?, ?> currentBuild : builds) {
        ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(currentBuild);
        AppDynamicsBuildAction performanceBuildAction = currentBuild.getAction(AppDynamicsBuildAction.class);
        if (performanceBuildAction == null) {
          continue;
        }
        AppDynamicsReport report = null;
        report = performanceBuildAction.getBuildActionResultsDisplay().getAppDynamicsReport();
        if (report == null) {
          nbBuildsToAnalyze--;
          continue;
        }
      /*
        dataSet.add(Math.round(report.getAverage()),
            Messages.ProjectAction_Average(), label);
        dataSet.add(Math.round(report.getMedian()),
            Messages.ProjectAction_Median(), label);
        dataSet.add(Math.round(report.get90Line()),
            Messages.ProjectAction_Line90(), label);
        dataSet.add(Math.round(report.getMin()),
            Messages.ProjectAction_Minimum(), label);
        dataSet.add(Math.round(report.getMax()),
            Messages.ProjectAction_Maximum(), label);
        dataSet.add(Math.round(report.errorPercent()),
            Messages.ProjectAction_PercentageOfErrors(), label);
        dataSet.add(Math.round(report.countErrors()),
            Messages.ProjectAction_Errors(), label);
            */
    }
    return dataSet;
  }


  /*
  public void doErrorsGraph(StaplerRequest request, StaplerResponse response)
          throws IOException {
      PerformanceReportPosition performanceReportPosition = new PerformanceReportPosition();
      request.bindParameters(performanceReportPosition);
      String performanceReportNameFile = performanceReportPosition.getPerformanceReportPosition();
      if (performanceReportNameFile == null) {
          if (getPerformanceReportList().size() == 1) {
              performanceReportNameFile = getPerformanceReportList().get(0);
          } else {
              return;
          }
      }
      if (ChartUtil.awtProblemCause != null) {
          // not available. send out error message
          response.sendRedirect2(request.getContextPath() + "/images/headless.png");
          return;
      }
      DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dataSetBuilderErrors = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();
      List<? extends AbstractBuild<?, ?>> builds = getProject().getBuilds();
      Range buildsLimits = getFirstAndLastBuild(request, builds);

      int nbBuildsToAnalyze = builds.size();
      for (AbstractBuild<?, ?> currentBuild :builds) {
          if (buildsLimits.in(nbBuildsToAnalyze)) {

              if (!buildsLimits.includedByStep(currentBuild.number)){
                  continue;
              }

              ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(currentBuild);
              PerformanceBuildAction performanceBuildAction = currentBuild.getAction(PerformanceBuildAction.class);
              if (performanceBuildAction == null) {
                  continue;
              }
              PerformanceReport performanceReport = performanceBuildAction.getPerformanceReportMap().getPerformanceReport(
                      performanceReportNameFile);
              if (performanceReport == null) {
                  nbBuildsToAnalyze--;
                  continue;
              }
              dataSetBuilderErrors.add(performanceReport.errorPercent(),
                      Messages.ProjectAction_Errors(), label);
          }
          nbBuildsToAnalyze--;
      }
      ChartUtil.generateGraph(request, response,
              createErrorsChart(dataSetBuilderErrors.build()), 400, 200);
  }
  */
}
