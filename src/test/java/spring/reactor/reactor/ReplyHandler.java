package spring.reactor.reactor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.event.Event;
import reactor.event.support.CallbackEvent;
import reactor.spring.context.annotation.Consumer;
import reactor.spring.context.annotation.ReplyTo;
import reactor.spring.context.annotation.Selector;
import reactor.spring.context.annotation.SelectorType;

/**
 *
 * @author Kent Yeh
 */
@Consumer
public class ReplyHandler {

    private static final Logger logger = LogManager.getLogger(ReplyHandler.class);

    @Selector(value = "string.reply.string", reactor = "@reactor")
    @ReplyTo()
    public String replyString(Event<String> evt) {
        logger.warn("ReplyHandler receive : [{}]", evt.getData());
        return "ReplyHandler reply after receive :" + evt.getData();
    }

    @Selector(value = "string.reply.exception", reactor = "@reactor")
    @ReplyTo()
    public Exception replyException1(Event<String> evt) {
        logger.warn("ReplyHandler receive : [{}]", evt.getData());
        return new RuntimeException("ReplyHandler reply after receive :" + evt.getData());
    }
    
    @Selector(value = "exception.reply.exception", reactor = "@reactor")
    @ReplyTo()
    public Exception replyException2(Event<String> evt) {
        logger.warn("ReplyHandler receive : [{}]", evt.getData());
        return new RuntimeException("ReplyHandler reply after receive :" + evt.getData());
    }

    @Selector(value = "reply_09\\d{8}", reactor = "@reactor", type = SelectorType.REGEX)
    public String replyRegex(Event<String> evt) {
        logger.error("ReplyHandler receive mobile no : [{}], but reply not works.", evt.getData());
        return evt.getData();
    }
    
    @Selector(value = "callback_09\\d{8}", reactor = "@reactor", type = SelectorType.REGEX)
    public void replyRegex(CallbackEvent<String> evt) {
        logger.error("ReplyHandler receive mobile no : [{}] and processing callback", evt.getData());
        evt.callback();
    }

    @Selector(value = "/reply/photo/{file}", reactor = "@reactor", type = SelectorType.URI)
    public String replyUri(Event<String> evt) {
        String file = evt.getHeaders().get("file");
        logger.warn("ReplyHandler receive URI:[{} for {}], but reply not works.", evt.getData(), file);
        return evt.getData();
    }

    @Selector(value = "$.headcount", reactor = "@jsonPathReactor", type = SelectorType.JSON_PATH)
    public String handleJsonpath(Event<String> evt) {
        logger.warn("SimpleHandler handle json-path:[{}], but reply not works.", evt.getData());
        return evt.getData();
    }

}
