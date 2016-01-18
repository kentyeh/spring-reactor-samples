package spring.reactor.processor;

import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class ProcessorHandler {

    @Selector(value = "spring.reactor.Pojo", reactor = "@reactor", type = SelectorType.TYPE)
    public void handlePojo(Pojo pojo) {
        log.warn("Processor final with [{}]", pojo);
    }

    @Selector(value = "reactor.core.processor.Operation", reactor = "@reactor", type = SelectorType.TYPE)
    public void handleOperation(Operation<String> operation) {
        log.info("Operation commit [{}]", operation.get());
        operation.commit();
    }
}
