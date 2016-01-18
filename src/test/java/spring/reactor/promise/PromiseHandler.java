package spring.reactor.promise;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.composable.Deferred;
import reactor.core.composable.Promise;
import reactor.spring.context.annotation.Consumer;
import reactor.spring.context.annotation.Selector;

/**
 *
 * @author Kent Yeh
 */
@Consumer
public class PromiseHandler {
    
    @Autowired
    private AtomicInteger serialno;
    @Selector(value = "promise2Handler", reactor = "@reactor")
    public void handlePromise(Deferred<String, Promise<String>> promise) {
        int serno = serialno.getAndIncrement();
        String msg = String.format("%4d.%s", serno, "Promise2 message");
        if (serno % 5 == 0) {
            promise.accept(new RuntimeException(msg));
        }else{
            promise.accept(msg);
        }
    }
}
