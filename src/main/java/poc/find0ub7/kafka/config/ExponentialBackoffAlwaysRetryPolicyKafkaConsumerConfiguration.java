package poc.find0ub7.kafka.config;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@RequiredArgsConstructor
public class ExponentialBackoffAlwaysRetryPolicyKafkaConsumerConfiguration {

  @Value("${spring.kafka.exponential-backoff-always-retry-policy.consumer.backoff-initial-interval:5000}")
  private Long backOffInitialInterval;

  @Value("${spring.kafka.exponential-backoff-always-retry-policy.consumer.backoff-max-interval:60000}")
  private Long backOffMaxInterval;

  @Value("${spring.kafka.exponential-backoff-always-retry-policy.consumer.backoff-multiplier:2.0}")
  private Double backOffMultiplier;

  @Value("${spring.kafka.exponential-backoff-always-retry-policy.concurrency:10}")
  private Integer concurrency;

  @Bean(name = "exponentialBackoffAlwaysRetryPolicyKafkaProperties")
  @ConfigurationProperties(prefix = "spring.kafka.exponential-backoff-always-retry-policy")
  public KafkaProperties exponentialBackoffAlwaysRetryPolicyKafkaProperties() {
    return new KafkaProperties();
  }

  @Bean(name = "exponentialBackoffAlwaysRetryPolicyConsumer")
  public ConsumerFactory<String, String> exponentialBackoffAlwaysRetryPolicyConsumer(
      final KafkaProperties exponentialBackoffAlwaysRetryPolicyKafkaProperties) {
    final Map<String, Object> exponentialBackoffAlwaysRetryPolicyConsumerProperties = exponentialBackoffAlwaysRetryPolicyKafkaProperties
        .buildConsumerProperties();

    final ConsumerFactory<String, String> exponentialBackoffAlwaysRetryPolicyConsumer = new DefaultKafkaConsumerFactory<>(
        exponentialBackoffAlwaysRetryPolicyConsumerProperties);

    return exponentialBackoffAlwaysRetryPolicyConsumer;
  }

  @Bean(name = "exponentialBackoffAlwaysRetryTemplate")
  public RetryTemplate exponentialBackoffAlwaysRetryTemplate() {
    final RetryTemplate exponentialBackoffAlwaysRetryTemplate = new RetryTemplate();
    final RetryPolicy alwaysRetryPolicy = new AlwaysRetryPolicy(); //infinite retry - if wish a specific retry number uses SimpleRetryPolicy
    final ExponentialBackOffPolicy exponentialBackoffPolicy = new ExponentialBackOffPolicy();
    exponentialBackoffPolicy.setInitialInterval(backOffInitialInterval);
    exponentialBackoffPolicy.setMaxInterval(backOffMaxInterval);
    exponentialBackoffPolicy.setMultiplier(backOffMultiplier);
    exponentialBackoffAlwaysRetryTemplate.setRetryPolicy(alwaysRetryPolicy);
    exponentialBackoffAlwaysRetryTemplate.setBackOffPolicy(exponentialBackoffPolicy);
    return exponentialBackoffAlwaysRetryTemplate;
  }

  @Bean(name = "exponentialBackoffAlwaysRetryPolicyContainerFactory")
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> exponentialBackoffAlwaysRetryPolicyContainerFactory(
      final ConsumerFactory<String, String> exponentialBackoffAlwaysRetryPolicyConsumer,
      final RetryTemplate exponentialBackoffAlwaysRetryTemplate) {
    final ConcurrentKafkaListenerContainerFactory<String, String> exponentialBackoffAlwaysRetryPolicyContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
    exponentialBackoffAlwaysRetryPolicyContainerFactory.setConcurrency(concurrency);
    exponentialBackoffAlwaysRetryPolicyContainerFactory.setConsumerFactory(exponentialBackoffAlwaysRetryPolicyConsumer);
    exponentialBackoffAlwaysRetryPolicyContainerFactory.setRetryTemplate(exponentialBackoffAlwaysRetryTemplate);
    return exponentialBackoffAlwaysRetryPolicyContainerFactory;
  }
}
