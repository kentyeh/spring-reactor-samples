package spring.reactor.dynamic;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.Environment;
import reactor.spring.context.config.EnableReactor;
import reactor.spring.factory.dynamic.DynamicReactorFactoryBean;

/**
 *
 * @author kent
 */
@Configuration
@EnableReactor
public class DynamicContext {

    @Autowired
    Environment env;

    @Bean
    public AtomicInteger serialno() {
        return new AtomicInteger(0);
    }

    @Bean(name = "dynamicStringDispatcher")
    DynamicReactorFactoryBean<DynamicDispatcher> dynamicDispatcher() {
        return new DynamicReactorFactoryBean(env, DynamicDispatcher.class);
    }

}
