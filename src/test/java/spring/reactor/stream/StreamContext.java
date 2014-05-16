package spring.reactor.stream;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import reactor.core.Environment;
import reactor.core.composable.Deferred;
import reactor.core.composable.Stream;
import reactor.core.composable.spec.Streams;
import reactor.spring.context.config.EnableReactor;

/**
 *
 * @author Kent Yeh
 */
@Configuration
@EnableReactor
public class StreamContext {

    @Autowired
    Environment env;

    @Bean
    public AtomicInteger serialno() {
        return new AtomicInteger(0);
    }

    @Bean
    @Scope("prototype")
    public Deferred<String, Stream<String>> simpleStream() {
        return Streams.defer(env, Environment.RING_BUFFER);
    }
}
