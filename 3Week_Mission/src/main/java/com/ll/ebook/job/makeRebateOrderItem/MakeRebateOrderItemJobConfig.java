package com.ll.ebook.job.makeRebateOrderItem;

import com.ll.ebook.app.order.entity.OrderItem;
import com.ll.ebook.app.order.repository.OrderItemRepository;
import com.ll.ebook.app.rebate.entity.RebateOrderItem;
import com.ll.ebook.app.rebate.repository.RebateOrderItemRepository;
import com.ll.ebook.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class MakeRebateOrderItemJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final OrderItemRepository orderItemRepository;
    private final RebateOrderItemRepository rebateOrderItemRepository;

    @Bean
    public Job makeRebateOrderItemJob(Step makeRebateOrderItemStep) throws Exception {
        return jobBuilderFactory.get("makeRebateOrderItemJob")
                .start(makeRebateOrderItemStep)
                .build();
    }

    @Bean
    @JobScope
    public Step makeRebateOrderItemStep(
            ItemReader orderItemReader,
            ItemProcessor orderItemToRebateOrderItemProcessor,
            ItemWriter rebateOrderItemWriter
    ) {
        return stepBuilderFactory.get("makeRebateOrderItemStep1")
                .<OrderItem, RebateOrderItem>chunk(100)
                .reader(orderItemReader)
                .processor(orderItemToRebateOrderItemProcessor)
                .writer(rebateOrderItemWriter)
                .build();
    }

    @StepScope
    @Bean
    public RepositoryItemReader<OrderItem> orderItemReader() {
//        String yearMonth = Ut.date.getYearMonth(); // 테스트용
        String yearMonth = Ut.date.getPrevYearMonth();
        int monthEndDay = Ut.date.getEndDayOf(yearMonth);

        LocalDateTime fromDate = Ut.date.parse(yearMonth + "-01 00:00:00.000000");
        LocalDateTime toDate = Ut.date.parse(yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay));

        return new RepositoryItemReaderBuilder<OrderItem>()
                .name("orderItemReader")
                .repository(orderItemRepository)
                .methodName("findAllByPayDateBetween")
                .pageSize(100)
                .arguments(Arrays.asList(fromDate, toDate))
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<OrderItem, RebateOrderItem> orderItemToRebateOrderItemProcessor() {
        return orderItem -> new RebateOrderItem(orderItem);
    }

    @StepScope
    @Bean
    public ItemWriter<RebateOrderItem> rebateOrderItemWriter() {
        return items -> items.forEach(item -> {
            RebateOrderItem oldRebateOrderItem = rebateOrderItemRepository.findByOrderItemId(item.getOrderItem().getId()).orElse(null);

            if (oldRebateOrderItem != null) {
                rebateOrderItemRepository.delete(oldRebateOrderItem);
            }

            rebateOrderItemRepository.save(item);
        });
    }
}
