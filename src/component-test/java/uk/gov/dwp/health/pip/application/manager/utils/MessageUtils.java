package uk.gov.dwp.health.pip.application.manager.utils;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.QueueAttributeName;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import java.util.Map;

public class MessageUtils {

  private final AmazonSQS amazonSQS;
  private final String workflowRequestQueueUrl;

  public MessageUtils(String serviceEndpoint, String awsRegion, String workflowRequestQueueUrl) {
    var endpointConfiguration =
        new AwsClientBuilder.EndpointConfiguration(serviceEndpoint, awsRegion);

    amazonSQS =
        AmazonSQSClientBuilder.standard().withEndpointConfiguration(endpointConfiguration).build();

    this.workflowRequestQueueUrl = workflowRequestQueueUrl;
  }

  public String getWorkflowRequestMessageCount() {
    var queueAttributes = getQueueAttributes(workflowRequestQueueUrl);
    return queueAttributes.get(QueueAttributeName.ApproximateNumberOfMessages.toString());
  }

  private Map<String, String> getQueueAttributes(String queueUrl) {
    var getQueueAttributesRequest =
        new GetQueueAttributesRequest(queueUrl).withAttributeNames(QueueAttributeName.All);
    return amazonSQS.getQueueAttributes(getQueueAttributesRequest).getAttributes();
  }

  public Message getWorkflowRequestMessage() {
    return getMessage(workflowRequestQueueUrl);
  }

  private Message getMessage(String queueUrl) {
    var receiveMessageRequest =
        new ReceiveMessageRequest(queueUrl)
            .withMessageAttributeNames("All")
            .withMaxNumberOfMessages(1);
    var receiveMessageResult = amazonSQS.receiveMessage(receiveMessageRequest);

    return receiveMessageResult.getMessages().get(0);
  }

  public void purgeWorkflowRequestQueue() {
    amazonSQS.purgeQueue(new PurgeQueueRequest(workflowRequestQueueUrl));
  }

}
