package com.miw.gildedrose.config;

import com.miw.gildedrose.repository.ItemRepository;
import com.miw.gildedrose.service.ItemClickBucketProvider;
import com.miw.gildedrose.strategy.ItemPriceStrategy;
import com.miw.gildedrose.strategy.NoOpPriceStrategy;
import com.miw.gildedrose.strategy.SurgePriceStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Main configuration class
 *
 * @author ssaqib
 * @since v0.1
 */
@Configuration
@EnableAsync
@RequiredArgsConstructor
public class GildedRoseConfiguration {

    private final ItemClickBucketProvider itemClickBucketProvider;
    private final ItemRepository itemRepository;

    @Bean
    @ConditionalOnProperty(name = "gildedRose.item.pricingStrategy", havingValue = "surge")
    ItemPriceStrategy surgePriceStrategy() {
        return new SurgePriceStrategy(itemClickBucketProvider, itemRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    ItemPriceStrategy itemPriceStrategy() {
        return new NoOpPriceStrategy();
    }

    /**
     * for @Async tasks
     */
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("grAsync-");
        executor.initialize();
        return executor;
    }
}
