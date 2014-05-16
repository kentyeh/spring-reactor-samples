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
import reactor.event.support.CallbackEvent;
import reactor.function.Consumer;

/**
 *
 * @author kent
 */
@ContextConfiguration(classes = spring.reactor.reactor.ReactorContext.class)
public class CallbackTest extends AbstractTestNGSpringContextTests {

    private static final Logger logger = LogManager.getLogger(SimpleTest.class);

    @Autowired
    Reactor reactor;
    @Autowired
    Reactor jsonPathReactor;
    @Autowired
    private AtomicInteger serialno;

    @BeforeClass
    public void setup() {
    }

    @AfterClass
    public void teardown() {
    }

    @Test
    void testRegex() {
        String phoneno = String.format("09%08d", serialno.incrementAndGet());
        logger.info("CallbackTest send:{}", phoneno);
        CallbackEvent cbEvt = new CallbackEvent(new PhoneObject(phoneno), new Consumer<PhoneObject>() {

            @Override
            public void accept(PhoneObject originalData) {
                logger.warn("CallbackTest callback with original data :{}", originalData.phoneno);
            }
        });
        reactor.notify("callback_" + phoneno, cbEvt);
    }

    private class PhoneObject {

        private String phoneno;

        public PhoneObject(String phoneno) {
            this.phoneno = phoneno;
        }

    }
}
