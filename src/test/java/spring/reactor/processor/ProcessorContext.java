package spring.reactor.processor;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.processor.Processor;
import reactor.core.processor.spec.ProcessorSpec;
import reactor.core.spec.Reactors;
import reactor.event.Event;
import reactor.function.Consumer;
import reactor.function.Supplier;
import reactor.spring.context.config.EnableReactor;
import spring.reactor.Pojo;

/**
 *
 * @author Kent Yeh
 */
@Configuration
@ComponentScan(basePackages = "spring.reactor.processor")
@EnableReactor
public class ProcessorContext {

    public static final int BUFF_CNT = 16;
    @Autowired
    Environment env;

    @Bean
    public AtomicInteger serialno() {
        return new AtomicInteger(0);
    }

    @Bean
    public Reactor reactor() {
        return Reactors.reactor(env, Environment.RING_BUFFER);
    }

    @Bean(name = "singleProcPojo")
    @Scope("prototype")
    public Processor<Pojo> singleProcPojo() {
        return new ProcessorSpec<Pojo>().singleThreadedProducer()
                .dataSupplier(new Supplier<Pojo>() {
                    @Override
                    public Pojo get() {
                        return new Pojo();
                    }
                }).consume(new Consumer<Pojo>() {
                    @Override
                    public void accept(Pojo pojo) {
                        reactor().notify(Pojo.class, Event.wrap(pojo));
                    }
                })
                .dataBufferSize(BUFF_CNT).yieldingWaitStrategy().get();
    }

    @Bean(name = "multipleProcPojo")
    @Scope("prototype")
    Processor<Pojo> multipleProcPojo() {
        return new ProcessorSpec<Pojo>().multiThreadedProducer()
                .dataSupplier(new Supplier<Pojo>() {
                    @Override
                    public Pojo get() {
                        return new Pojo();
                    }
                }).consume(new Consumer<Pojo>() {
                    @Override
                    public void accept(Pojo pojo) {
                        reactor().notify(Pojo.class, Event.wrap(pojo));
                    }
                })
                .dataBufferSize(BUFF_CNT).yieldingWaitStrategy().get();
    }
}
