package spring.reactor.processor;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.processor.Operation;
import reactor.spring.context.annotation.Consumer;
import reactor.spring.context.annotation.Selector;
import reactor.spring.context.annotation.SelectorType;
import spring.reactor.Pojo;

/**
 *
 * @author Kent Yeh
 */
@Consumer
public class ProcessorHandler {

    private static final Logger logger = LogManager.getLogger(ProcessorHandler.class);

    @Autowired
    private AtomicInteger serialno;

    @Selector(value = "spring.reactor.Pojo", reactor = "@reactor", type = SelectorType.TYPE)
    public void handlePojo(Pojo pojo) {
        logger.warn("Processor final with [{}]", pojo);
    }

    @Selector(value = "reactor.core.processor.Operation", reactor = "@reactor", type = SelectorType.TYPE)
    public void handleOperation(Operation<String> operation) {
        logger.info("Operation commit [{}]", operation.get());
        operation.commit();
    }
}
