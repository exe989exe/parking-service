package ru.rakamov.parkingservice.config;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import ru.rakamov.parkingservice.dto.ParkingEntryDTO;
import ru.rakamov.parkingservice.entity.ParkingEntity;
import ru.rakamov.parkingservice.repository.ParkingRepository;


import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;

@Configuration
public class BatchConfig {

    @Bean
    public FlatFileItemReader<ParkingEntryDTO> reader() {
        return new FlatFileItemReaderBuilder<ParkingEntryDTO>()
                .name("parkingEntryReader")
                .resource(new ClassPathResource("entries.csv"))
                .delimited()
                .names("carNumber", "vehicleType", "entryTime")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(ParkingEntryDTO.class);
                }})
                .build();
    }

    @Bean
    public ParkingEntryProcessor processor() {
        return new ParkingEntryProcessor();
    }

    @Bean
    public RepositoryItemWriter<ParkingEntity> writer(ParkingRepository repository) {
        RepositoryItemWriter<ParkingEntity> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                      FlatFileItemReader<ParkingEntryDTO> reader, ParkingEntryProcessor processor,
                      RepositoryItemWriter<ParkingEntity> writer) {
        return new StepBuilder("step1", jobRepository)
                .<ParkingEntryDTO, ParkingEntity>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job importJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("importParkingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }

    public static class ParkingEntryProcessor implements ItemProcessor<ParkingEntryDTO, ParkingEntity> {
        @Override
        public ParkingEntity process(ParkingEntryDTO item) {
            ParkingEntity entity = new ParkingEntity();
            entity.setCarNumber(item.getCarNumber());
            entity.setVehicleType(item.getVehicleType());
            entity.setEntryTime(LocalDateTime.parse(item.getEntryTime()));
            return entity;
        }
    }
}