package io.eventuate.local.unified.cdc.pipeline.dblog.mysqlbinlog.factory;

import io.eventuate.javaclient.spring.jdbc.EventuateSchema;
import io.eventuate.local.common.PublishedEvent;
import io.eventuate.local.common.PublishedEventPublishingStrategy;
import io.eventuate.local.common.PublishingStrategy;
import io.eventuate.local.db.log.common.DatabaseOffsetKafkaStore;
import io.eventuate.local.db.log.common.OffsetStore;
import io.eventuate.local.db.log.common.PublishingFilter;
import io.eventuate.local.java.common.broker.DataProducerFactory;
import io.eventuate.local.java.kafka.EventuateKafkaConfigurationProperties;
import io.eventuate.local.java.kafka.consumer.EventuateKafkaConsumerConfigurationProperties;
import io.eventuate.local.java.kafka.producer.EventuateKafkaProducer;
import io.eventuate.local.mysql.binlog.IWriteRowsEventDataParser;
import io.eventuate.local.mysql.binlog.SourceTableNameSupplier;
import io.eventuate.local.mysql.binlog.WriteRowsEventDataParser;
import io.eventuate.local.unified.cdc.pipeline.dblog.mysqlbinlog.properties.MySqlBinlogCdcPipelineProperties;
import org.apache.curator.framework.CuratorFramework;

import javax.sql.DataSource;

public class MySqlBinlogCdcPipelineFactory extends AbstractMySqlBinlogCdcPipelineFactory<PublishedEvent> {
  public static final String TYPE = "eventuate-local-mysql-binlog";

  public MySqlBinlogCdcPipelineFactory(CuratorFramework curatorFramework,
                                       DataProducerFactory dataProducerFactory,
                                       EventuateKafkaConfigurationProperties eventuateKafkaConfigurationProperties,
                                       EventuateKafkaConsumerConfigurationProperties eventuateKafkaConsumerConfigurationProperties,
                                       EventuateKafkaProducer eventuateKafkaProducer,
                                       PublishingFilter publishingFilter) {
    super(curatorFramework,
            dataProducerFactory,
            eventuateKafkaConfigurationProperties,
            eventuateKafkaConsumerConfigurationProperties,
            eventuateKafkaProducer,
            publishingFilter);
  }


  @Override
  public boolean supports(String type) {
    return TYPE.equals(type);
  }

  @Override
  protected IWriteRowsEventDataParser<PublishedEvent> createWriteRowsEventDataParser(EventuateSchema eventuateSchema,
                                                                                     DataSource dataSource,
                                                                                     SourceTableNameSupplier sourceTableNameSupplier) {

    return new WriteRowsEventDataParser(dataSource, sourceTableNameSupplier.getSourceTableName(), eventuateSchema);
  }

  @Override
  protected SourceTableNameSupplier createSourceTableNameSupplier(MySqlBinlogCdcPipelineProperties mySqlBinlogCdcPipelineProperties) {
    return new SourceTableNameSupplier(mySqlBinlogCdcPipelineProperties.getSourceTableName(), "EVENTS");
  }

  @Override
  protected OffsetStore createOffsetStore(MySqlBinlogCdcPipelineProperties properties,
                                          DataSource dataSource,
                                          EventuateSchema eventuateSchema) {

    return new DatabaseOffsetKafkaStore(properties.getDbHistoryTopicName(),
            properties.getMySqlBinLogClientName(),
            eventuateKafkaProducer,
            eventuateKafkaConfigurationProperties,
            eventuateKafkaConsumerConfigurationProperties);
  }

  @Override
  protected PublishingStrategy<PublishedEvent> createPublishingStrategy() {
    return new PublishedEventPublishingStrategy();
  }
}