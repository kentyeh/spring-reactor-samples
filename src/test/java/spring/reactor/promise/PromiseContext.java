package spring.reactor.promise;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.spring.context.config.EnableReactor;

/**
 *
 * @author Kent Yeh
 */
@Configuration
@ComponentScan(basePackages = "spring.reactor.promise")
@EnableReactor
public class PromiseContext {

    @Bean
    public AtomicInteger serialno() {
        return new AtomicInteger(0);
    }

    @Bean
    @Scope("prototype")
    public Reactor reactor(Environment env) {
        return Reactors.reactor(env, Environment.RING_BUFFER);
    }

}
