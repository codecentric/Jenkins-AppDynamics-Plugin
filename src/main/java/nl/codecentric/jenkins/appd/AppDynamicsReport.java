package nl.codecentric.jenkins.appd;

import hudson.model.AbstractBuild;
import nl.codecentric.jenkins.appd.rest.types.MetricData;
import nl.codecentric.jenkins.appd.rest.types.MetricValues;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

/** Represents a single performance report */
public class AppDynamicsReport {

  private final List<MetricData> metricDataList = new ArrayList<MetricData>();
  private final Long reportTimestamp;
  private final Integer reportDurationInMinutes;

  private AppDynamicsBuildAction buildAction;
  private AppDynamicsReport lastBuildReport;

  public AppDynamicsReport(final Long timestamp, final Integer durationInMinutes) {
    this.reportTimestamp = timestamp;
    this.reportDurationInMinutes = durationInMinutes;
  }

  public void addMetrics(final MetricData metrics) {
    metricDataList.add(metrics);
  }

  public List<MetricData> getMetricsList() {
    return metricDataList;
  }


  public double errorPercent() {
//    if (buildAction.getPerformanceReportMap().ifSummarizerParserUsed(reportFileName))  {
//      return size() == 0 ? 0 : ((double) countErrors()) / size();
//    } else {
//      return size() == 0 ? 0 : ((double) countErrors()) / size() * 100;
//    }
    return 5.3;
  }

  //
  public long getAverage() {
    long result = 0;
    long total = 0;
    for (MetricValues value : this.metricDataList.get(0).getMetricValues()) {
      total += value.getValue();
    }

    if (this.metricDataList.get(0).getMetricValues().size() > 0) {
      result = total / this.metricDataList.get(0).getMetricValues().size();
    }

    return result;
  }

  public long getMax() {
    long max = Long.MIN_VALUE;
//    for (UriReport currentReport : uriReportMap.values()) {
//      max = Math.max(currentReport.getMax(), max);
//    }
    max = 83700;
    return max;
  }

  public long getMin() {
    long min = Long.MAX_VALUE;
//    for (UriReport currentReport : uriReportMap.values()) {
//      min = Math.min(currentReport.getMin(), min);
//    }
    min = 3480;
    return min;
  }

//  public long getAverageDiff() {
//    if ( lastBuildReport == null ) {
//      return 0;
//    }
//    return getAverage() - lastBuildReport.getAverage();
//  }
//
//  public long getMedianDiff() {
//    if ( lastBuildReport == null ) {
//      return 0;
//    }
//    return getMedian() - lastBuildReport.getMedian();
//  }
//
//  public double getErrorPercentDiff() {
//    if ( lastBuildReport == null ) {
//      return 0;
//    }
//    return errorPercent() - lastBuildReport.errorPercent();
//  }
//
//  public String getLastBuildHttpCodeIfChanged() {
//    return "";
//  }
//
//  public int getSizeDiff() {
//    if ( lastBuildReport == null ) {
//      return 0;
//    }
//    return size() - lastBuildReport.size();
//  }

  public String getName() {
    DateTimeFormatter dateTimeFormat = DateTimeFormat.mediumDateTime();
    return String.format("AppDynamics Metric Report for time %s - with a duration of %d minutes",
      dateTimeFormat.print(this.reportTimestamp), reportDurationInMinutes);
  }

  public AbstractBuild<?, ?> getBuild() {
    return buildAction.getBuild();
  }

  AppDynamicsBuildAction getBuildAction() {
    return buildAction;
  }

  void setBuildAction(AppDynamicsBuildAction buildAction) {
    this.buildAction = buildAction;
  }

  public void setLastBuildReport(AppDynamicsReport lastBuildReport) {
    this.lastBuildReport = lastBuildReport;
  }

}
