package spring.reactor.reactor;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class ReactorReplyTest extends AbstractTestNGSpringContextTests {

    private static final Logger logger = LogManager.getLogger(SimpleTest.class);

    @Autowired
    Reactor reactor;
    @Autowired
    Reactor jsonPathReactor;
    private AtomicInteger cnt = new AtomicInteger(100);

    @BeforeClass
    public void setup() {
    }

    @AfterClass
    public void teardown() {
    }
    @Test
    void testString() {
        String msg = String.format("%3d.%s", cnt.incrementAndGet(), "Hello World");
        logger.info("ReactorTest send:{}", msg);
        reactor.notify("simple.string", Event.wrap(msg).setReplyTo("simple.string"));
    }
}
