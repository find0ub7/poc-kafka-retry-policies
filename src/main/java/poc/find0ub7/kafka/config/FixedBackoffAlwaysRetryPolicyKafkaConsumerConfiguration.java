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
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@RequiredArgsConstructor
public class FixedBackoffAlwaysRetryPolicyKafkaConsumerConfiguration {

  @Value("${spring.kafka.fixed-backoff-always-retry-policy.consumer.back-off-period-ms:5000}")
  private Long backOffPeriodInMs;

  @Value("${spring.kafka.fixed-backoff-always-retry-policy.concurrency:10}")
  private Integer concurrency;

  @Bean(name = "fixedBackoffAlwaysRetryPolicyKafkaProperties")
  @ConfigurationProperties(prefix = "spring.kafka.fixed-backoff-always-retry-policy")
  public KafkaProperties fixedBackoffAlwaysRetryPolicyKafkaProperties() {
    return new KafkaProperties();
  }

  @Bean(name = "fixedBackoffAlwaysRetryPolicyConsumer")
  public ConsumerFactory<String, String> fixedBackoffAlwaysRetryPolicyConsumer(
      final KafkaProperties fixedBackoffAlwaysRetryPolicyKafkaProperties) {
    final Map<String, Object> fixedBackoffAlwaysRetryPolicyConsumerProperties = fixedBackoffAlwaysRetryPolicyKafkaProperties
        .buildConsumerProperties();

    final ConsumerFactory<String, String> fixedBackoffAlwaysRetryPolicyConsumer = new DefaultKafkaConsumerFactory<>(
        fixedBackoffAlwaysRetryPolicyConsumerProperties);

    return fixedBackoffAlwaysRetryPolicyConsumer;
  }

  @Bean(name = "fixedBackoffAlwaysRetryTemplate")
  public RetryTemplate fixedBackoffAlwaysRetryTemplate() {
    final RetryTemplate fixedBackoffAlwaysRetryTemplate = new RetryTemplate();
    final RetryPolicy alwaysRetryPolicy = new AlwaysRetryPolicy(); //infinite retry - if wish a specific retry number uses SimpleRetryPolicy
    final FixedBackOffPolicy fixedBackoffPolicy = new FixedBackOffPolicy();
    fixedBackoffPolicy.setBackOffPeriod(backOffPeriodInMs);
    fixedBackoffAlwaysRetryTemplate.setRetryPolicy(alwaysRetryPolicy);
    fixedBackoffAlwaysRetryTemplate.setBackOffPolicy(fixedBackoffPolicy);
    return fixedBackoffAlwaysRetryTemplate;
  }

  @Bean(name = "fixedBackoffAlwaysRetryPolicyContainerFactory")
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> fixedBackoffAlwaysRetryPolicyContainerFactory(
      final ConsumerFactory<String, String> fixedBackoffAlwaysRetryPolicyConsumer,
      final RetryTemplate fixedBackoffAlwaysRetryTemplate) {
    final ConcurrentKafkaListenerContainerFactory<String, String> fixedBackoffAlwaysRetryPolicyContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
    fixedBackoffAlwaysRetryPolicyContainerFactory.setConcurrency(concurrency);
    fixedBackoffAlwaysRetryPolicyContainerFactory.setConsumerFactory(fixedBackoffAlwaysRetryPolicyConsumer);
    fixedBackoffAlwaysRetryPolicyContainerFactory.setRetryTemplate(fixedBackoffAlwaysRetryTemplate);
    return fixedBackoffAlwaysRetryPolicyContainerFactory;
  }
}
