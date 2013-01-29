package nl.codecentric.jenkins.appd;

import hudson.model.AbstractBuild;
import nl.codecentric.jenkins.appd.rest.types.MetricData;
import nl.codecentric.jenkins.appd.rest.types.MetricValues;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a single performance report
 */
public class AppDynamicsReport {

  private final Map<String, MetricData> keyedMetricDataMap = new LinkedHashMap<String, MetricData>();
  private final Long reportTimestamp;
  private final Integer reportDurationInMinutes;

  private AppDynamicsBuildAction buildAction;
  private AppDynamicsReport lastBuildReport;

  public AppDynamicsReport(final Long timestamp, final Integer durationInMinutes) {
    this.reportTimestamp = timestamp;
    this.reportDurationInMinutes = durationInMinutes;
  }

  public void addMetrics(final MetricData metrics) {
    keyedMetricDataMap.put(metrics.getMetricPath(), metrics);
  }

  public MetricData getMetricByKey(final String metricKey) {
    final MetricData selectedMetric = keyedMetricDataMap.get(metricKey);
    if (selectedMetric == null) {
      throw new IllegalArgumentException("Provided Metric Key is not available, tried to select; " + metricKey);
    }
    return selectedMetric;
  }

  public List<MetricData> getMetricsList() {
    return new ArrayList<MetricData>(keyedMetricDataMap.values());
  }

  public double getAverageForMetric(final String metricKey) {
    final MetricData selectedMetric = getMetricByKey(metricKey);

    long calculatedSum = 0;
    for (MetricValues value : selectedMetric.getMetricValues()) {
      calculatedSum += value.getValue();
    }

    final int numberOfMeasurements = selectedMetric.getMetricValues().size();
    double result = -1;
    if (numberOfMeasurements > 0) {
      result = calculatedSum / numberOfMeasurements;
    }

    return result;
  }

  public long getMaxForMetric(final String metricKey) {
    final MetricData selectedMetric = getMetricByKey(metricKey);

    long max = Long.MIN_VALUE;
    for (MetricValues value : selectedMetric.getMetricValues()) {
      max = Math.max(value.getMax(), max);
    }
    return max;
  }

  public long getMinForMetric(final String metricKey) {
    final MetricData selectedMetric = getMetricByKey(metricKey);

    long min = Long.MAX_VALUE;
    for (MetricValues value : selectedMetric.getMetricValues()) {
      min = Math.min(value.getMin(), min);
    }
    return min;
  }

  public String getName() {
    DateTimeFormatter dateTimeFormat = DateTimeFormat.mediumDateTime();
    return String.format("AppDynamics Metric Report for time %s - with a duration of %d minutes",
        dateTimeFormat.print(this.reportTimestamp), reportDurationInMinutes);
  }

  public long getTimestamp() {
    return reportTimestamp;
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
