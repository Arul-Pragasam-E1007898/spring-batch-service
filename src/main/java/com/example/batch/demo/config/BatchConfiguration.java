package com.example.batch.demo.config;

import com.example.batch.demo.core.ContactEnricher;
import com.example.batch.demo.core.ContactValidator;
import com.example.batch.demo.core.JobCompletionNotificationListener;
import com.example.batch.demo.model.Contact;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

  @Autowired
  private JobBuilderFactory jobBuilderFactory;

  @Autowired
  private StepBuilderFactory stepBuilderFactory;

  @Autowired
  private JobCompletionNotificationListener listener;
  @Bean
  @StepScope
  public FlatFileItemReader<Contact> reader(@Value("#{jobParameters[file]}") String file) {
    return new FlatFileItemReaderBuilder<Contact>()
        .name("personItemReader")
        .resource(new FileSystemResource(file))
        .delimited()
        .names(new String[]{"firstName", "lastName"})
        .fieldSetMapper(new BeanWrapperFieldSetMapper<Contact>() {{
          setTargetType(Contact.class);
        }})
        .build();
  }

  @Bean
  public ContactValidator validator() {
    return new ContactValidator();
  }

  @Bean
  public ContactEnricher enricher() {
    return new ContactEnricher();
  }

  @Bean
  public CompositeItemProcessor<Contact, Contact> compositeProcessor() {
    List<ItemProcessor<Contact, Contact>> delegates = new ArrayList<>(2);
    delegates.add(enricher());
    delegates.add(validator());

    CompositeItemProcessor<Contact, Contact> processor = new CompositeItemProcessor<>();
    processor.setDelegates(delegates);
    return processor;
  }

  @Bean
  public JdbcBatchItemWriter<Contact> writer(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Contact>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .sql("INSERT INTO contacts ( id, first_name, last_name) VALUES ( :id, :firstName, :lastName)")
        .dataSource(dataSource)
        .build();
  }



  @Bean
  public Step step(JdbcBatchItemWriter<Contact> writer) {
    return stepBuilderFactory.get("step1")
        .<Contact, Contact> chunk(3)
        .reader(reader(null))
        .processor(compositeProcessor())
        .writer(writer)
        .build();
  }

  @Bean
  public Job importJob(Step step1) {
    return jobBuilderFactory.get("importJob")
        .incrementer(new RunIdIncrementer())
        .listener(listener)
        .flow(step1)
        .end()
        .build();
  }
}
