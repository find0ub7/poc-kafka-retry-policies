package poc.find0ub7.kafka.config;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Configuration
public class DefaultKafkaConfiguration {
  @Primary
  @Bean
  public KafkaProperties kafkaProperties() {
    return new KafkaProperties();
  }

  @Bean
  public <K, V> ConsumerFactory<K, V> consumerFactory(final KafkaProperties kafkaProperties) {
    return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
  }
}
