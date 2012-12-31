package nl.codecentric.jenkins.appd;

import hudson.model.AbstractBuild;
import hudson.model.ModelObject;
import hudson.model.TaskListener;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.DataSetBuilder;
import nl.codecentric.jenkins.appd.util.LocalMessages;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/** Root object of a AppDynamics Build Report. */
public class BuildActionResultsDisplay implements ModelObject {

  /** The {@link AppDynamicsBuildAction} that this report belongs to. */
  private transient AppDynamicsBuildAction buildAction;

  private static AbstractBuild<?, ?> currentBuild = null;

  private AppDynamicsReport currentReport;

  /**
   * Parses the reports and build a {@link BuildActionResultsDisplay}.
   * @throws java.io.IOException If a report fails to parse.
   */
  BuildActionResultsDisplay(final AppDynamicsBuildAction buildAction, TaskListener listener)
      throws IOException {
    this.buildAction = buildAction;


    AppDynamicsReport pregenReport = this.buildAction.getAppDynamicsReport();

    pregenReport.setBuildAction(buildAction);
    currentReport = pregenReport;
    // Get the data (again) to show the results actually in a table / graph
    // TODO: find some way to not fetch the same data twice from the REST interface but re-use the report.


    addPreviousBuildReports();
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


  /**
   * <p>
   * Verify if the AppDynamicsReport exist the AppDynamicsReportName must to be like it
   * is in the build
   * </p>
   * @return boolean
   */
  public boolean isFailed() {
    return getAppDynamicsReport() == null;
  }

    // TODO rename to correctly render graph
  public void doRespondingTimeGraphStub(StaplerRequest request,
                                    StaplerResponse response) throws IOException {
    String parameter = request.getParameter("AppDynamicsReportPosition");
    AbstractBuild<?, ?> previousBuild = getBuild();
    final Map<AbstractBuild<?, ?>, AppDynamicsReport> buildReport = new LinkedHashMap<AbstractBuild<?, ?>, AppDynamicsReport>();
    while (previousBuild != null) {
      final AbstractBuild<?, ?> currentBuild = previousBuild;
//      parseReports(currentBuild, TaskListener.NULL, new AppDynamicsReportCollector() {
//        public void add(AppDynamicsReport parse) {
//          buildReport.put(currentBuild, parse);
//        }
//      }, parameter);
      previousBuild = previousBuild.getPreviousBuild();
    }
    //Now we should have the data necessary to generate the graphs!
    DataSetBuilder<String, NumberOnlyBuildLabel> dataSetBuilderAverage = new DataSetBuilder<String, NumberOnlyBuildLabel>();
    for (AbstractBuild<?, ?> currentBuild : buildReport.keySet()) {
      NumberOnlyBuildLabel label = new NumberOnlyBuildLabel(currentBuild);
      AppDynamicsReport report = buildReport.get(currentBuild);
      // TODO           dataSetBuilderAverage.add(report.getAverage(), Messages.ProjectAction_Average(), label);
    }
// TODO        ChartUtil.generateGraph(request, response, PerformanceProjectAction.createRespondingTimeChart(dataSetBuilderAverage.build()), 400, 200);
  }

  public void doSummarizerGraph(StaplerRequest request,
                                StaplerResponse response) throws IOException {
    String parameter = request.getParameter("AppDynamicsReportPosition");
    AbstractBuild<?, ?> previousBuild = getBuild();
    final Map<AbstractBuild<?, ?>, AppDynamicsReport> buildReport = new LinkedHashMap<AbstractBuild<?, ?>, AppDynamicsReport>();

    while (previousBuild != null) {
      final AbstractBuild<?, ?> currentBuild = previousBuild;
//      parseReports(currentBuild, TaskListener.NULL, new AppDynamicsReportCollector() {
//
//        public void add(AppDynamicsReport parse) {
//          buildReport.put(currentBuild, parse);
//        }
//      }, parameter);
      previousBuild = previousBuild.getPreviousBuild();
    }
    DataSetBuilder<NumberOnlyBuildLabel, String> dataSetBuilderSummarizer = new DataSetBuilder<NumberOnlyBuildLabel, String>();
    for (AbstractBuild<?, ?> currentBuild : buildReport.keySet()) {
      NumberOnlyBuildLabel label = new NumberOnlyBuildLabel(currentBuild);
      AppDynamicsReport report = buildReport.get(currentBuild);

      //Now we should have the data necessary to generate the graphs!
// TODO          for (String key:report.getUriReportMap().keySet()) {
//               Long methodAvg=report.getUriReportMap().get(key).getHttpSampleList().get(0).getDuration();
//               dataSetBuilderSummarizer.add(methodAvg, label, key);
//           };
    }
// TODO       ChartUtil.generateGraph(request, response, PerformanceProjectAction.createSummarizerChart(dataSetBuilderSummarizer.build(), "ms", Messages.ProjectAction_RespondingTime()), 400, 200);
  }


  private void addPreviousBuildReports() {

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


}
