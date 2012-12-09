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
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/** Root object of a AppDynamics Build Report. */
public class BuildActionResultsDisplay implements ModelObject {

  /** The {@link AppDynamicsBuildAction} that this report belongs to. */
  private transient AppDynamicsBuildAction buildAction;

  private static final String PERFORMANCE_REPORTS_DIRECTORY = "performance-reports";

  private static AbstractBuild<?, ?> currentBuild = null;

  private AppDynamicsReport currentReport;

  /**
   * Parses the reports and build a {@link BuildActionResultsDisplay}.
   * @throws java.io.IOException If a report fails to parse.
   */
  BuildActionResultsDisplay(final AppDynamicsBuildAction buildAction, TaskListener listener)
      throws IOException {
    this.buildAction = buildAction;
    parseReports(getBuild(), listener, new AppDynamicsReportCollector() {

      public void add(AppDynamicsReport report) {
        report.setBuildAction(buildAction);
        currentReport = report;
      }
    }, null);
  }

  private void addAll(AppDynamicsReport report) {
    report.setBuildAction(buildAction);
    currentReport = report;
  }

  public AbstractBuild<?, ?> getBuild() {
    return buildAction.getBuild();
  }

  AppDynamicsBuildAction getBuildAction() {
    return buildAction;
  }

  public String getDisplayName() {
    return LocalMessages.REPORT_DISPLAYNAME.toString();
  }

  public AppDynamicsReport getAppDynamicsReport() {
    return currentReport;
  }

  void setBuildAction(AppDynamicsBuildAction buildAction) {
    this.buildAction = buildAction;
  }

  public void setAppDynamicsReport(AppDynamicsReport appDynamicsReport) {
    this.currentReport = appDynamicsReport;
  }

  public static String getAppDynamicsReportFileRelativePath(
      String parserDisplayName, String reportFileName) {
    return getRelativePath(parserDisplayName, reportFileName);
  }

  public static String getAppDynamicsReportDirRelativePath() {
    return getRelativePath();
  }

  private static String getRelativePath(String... suffixes) {
    StringBuilder sb = new StringBuilder(100);
    sb.append(PERFORMANCE_REPORTS_DIRECTORY);
    for (String suffix : suffixes) {
      sb.append(File.separator).append(suffix);
    }
    return sb.toString();
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
      parseReports(currentBuild, TaskListener.NULL, new AppDynamicsReportCollector() {
        public void add(AppDynamicsReport parse) {
          buildReport.put(currentBuild, parse);
        }
      }, parameter);
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
      parseReports(currentBuild, TaskListener.NULL, new AppDynamicsReportCollector() {

        public void add(AppDynamicsReport parse) {
          buildReport.put(currentBuild, parse);
        }
      }, parameter);
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


  /*
   * TODO Seems like files are parsed again in this method.. Do we need this? Perhaps for previous builds?
   */
  private void parseReports(AbstractBuild<?, ?> build, TaskListener listener, AppDynamicsReportCollector collector, final String filename) throws IOException {
            AppDynamicsDataCollector p = buildAction.getCollector();
              collector.add(p.parse(build, listener));


//    File repo = new File(build.getRootDir(),
//        BuildActionResultsDisplay.getAppDynamicsReportDirRelativePath());
//
//    // files directly under the directory are for JMeter, for compatibility reasons.
//    File[] files = repo.listFiles(new FileFilter() {
//
//      public boolean accept(File f) {
//        return !f.isDirectory();
//      }
//    });
//    // this may fail, if the build itself failed, we need to recover gracefully
////        if (files != null) {
////            addAll(new JMeterParser("").parse(build,
////                    Arrays.asList(files), listener));
////        }
//
//    // otherwise subdirectory name designates the parser ID.
//    File[] dirs = repo.listFiles(new FileFilter() {
//
//      public boolean accept(File f) {
//        return f.isDirectory();
//      }
//    });
//    // this may fail, if the build itself failed, we need to recover gracefully
//    if (dirs != null) {
//      for (File dir : dirs) {
//        AppDynamicsDataCollector p = buildAction.getCollector();
//        if (p != null) {
//          File[] listFiles = dir.listFiles(new FilenameFilter() {
//
//            public boolean accept(File dir, String name) {
//              if (filename == null) {
//                return true;
//              }
//              if (name.equals(filename)) {
//                return true;
//              }
//              return false;
//            }
//          });
//          collector.add(p.parse(build, listener));
//        }
//      }
//    }

    addPreviousBuildReports();
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


  private interface AppDynamicsReportCollector {

    public void add(AppDynamicsReport parse);
  }


}
