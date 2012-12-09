package nl.codecentric.jenkins.appd;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
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
