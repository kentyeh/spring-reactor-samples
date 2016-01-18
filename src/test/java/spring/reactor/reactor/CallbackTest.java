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
import reactor.event.support.CallbackEvent;
import reactor.function.Consumer;

/**
 *
 * @author kent
 */
@ContextConfiguration(classes = spring.reactor.reactor.ReactorContext.class)
@Log4j2
public class CallbackTest extends AbstractTestNGSpringContextTests {

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
        log.info("CallbackTest send:{}", phoneno);
        CallbackEvent cbEvt = new CallbackEvent(new PhoneObject(phoneno), new Consumer<PhoneObject>() {

            @Override
            public void accept(PhoneObject originalData) {
                log.warn("CallbackTest callback with original data :{}", originalData.phoneno);
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
