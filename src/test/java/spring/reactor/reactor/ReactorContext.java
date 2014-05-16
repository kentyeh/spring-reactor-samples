package spring.reactor.reactor;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.spring.context.config.EnableReactor;

/**
 *
 * @author kent
 */
@Configuration
@ComponentScan(basePackages = "spring.reactor.reactor")
@EnableReactor
public class ReactorContext {

    @Autowired
    Environment env;

    @Bean
    public AtomicInteger serialno() {
        return new AtomicInteger(0);
    }

    @Bean
    public Reactor reactor(Environment env) {
        return Reactors.reactor(env, Environment.RING_BUFFER);
    }

    @Bean
    public Reactor jsonPathReactor(Environment env) {
        return Reactors.reactor(env, Environment.RING_BUFFER);
    }
}
