package com.nakedwines.displayscreen;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;

@RestController("/aws")
public class AwsDataController {
	@Value("aws.access_key")
	private String accessKey;
	@Value("aws.secret_key")
	private String secretKey;
	
	@Autowired
	private AmazonCloudWatchClient cloudWatchClient;
	
	@RequestMapping("/{metric}")
	public List<Datapoint> cloudwatchData(String metric){
		GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
        .withStartTime(new DateTime().minusMinutes(30).toDate())
        .withNamespace("AWS/SNS")
        .withPeriod(60 * 5)//5 mins
        .withStatistics("Sum")
        .withMetricName(metric)
        .withEndTime(new Date());
		
		GetMetricStatisticsResult result = cloudWatchClient.getMetricStatistics(request);
		return result.getDatapoints();
	}
}