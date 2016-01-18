package spring.reactor.reactor;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import reactor.core.Reactor;
import reactor.event.Event;

/**
 *
 * @author Kent Yeh
 */
@ContextConfiguration(classes = spring.reactor.reactor.ReactorContext.class)
@Log4j2
public class ReactorReplyTest extends AbstractTestNGSpringContextTests {

    @Autowired
    Reactor reactor;
    @Autowired
    Reactor jsonPathReactor;
    private final AtomicInteger cnt = new AtomicInteger(100);

    @BeforeClass
    public void setup() {
    }

    @AfterClass
    public void teardown() {
    }
    @Test
    void testString() {
        String msg = String.format("%3d.%s", cnt.incrementAndGet(), "Hello World");
        log.info("ReactorTest send:{}", msg);
        reactor.notify("simple.string", Event.wrap(msg).setReplyTo("simple.string"));
    }
}
