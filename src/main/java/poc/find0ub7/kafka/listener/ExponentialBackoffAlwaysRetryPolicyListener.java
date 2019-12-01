package poc.find0ub7.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExponentialBackoffAlwaysRetryPolicyListener {

  @KafkaListener(topics = "${spring.kafka.topic:poc-find0ub7-kafka}",
      groupId = "${spring.kafka.exponential-backoff-always-retry-policy.consumer.group-id:exponential-backoff-always-retry-policy-group-id}",
      containerFactory = "exponentialBackoffAlwaysRetryPolicyContainerFactory")
  public void onMessage(@Payload final String message,
      @Header(KafkaHeaders.RECEIVED_TOPIC) final String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION_ID) final String partitionId,
      @Header(KafkaHeaders.OFFSET) final Long offset) {
    log.info("[ExponentialBackoffAlwaysRetryPolicyListener] Message received from topic/partition/offset ({}/{}/{}): {}", topic, partitionId,
        offset, message);

    //apenas para simular um runtime exception e testar o funcionamento do retry
    if ("exception".equalsIgnoreCase(message)) {
      throw new RuntimeException("Some runtime exception occurs");
    }
  }
}
