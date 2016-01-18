package spring.reactor.reactor;

import lombok.extern.log4j.Log4j2;
import reactor.event.Event;
import reactor.spring.context.annotation.Consumer;
import reactor.spring.context.annotation.Selector;
import reactor.spring.context.annotation.SelectorType;

/**
 *
 * @author Kent Yeh
 */
@Consumer
@Log4j2
public class SimpleHandler {

    @Selector(value = "simple.string", reactor = "@reactor")
    public void handleStringEvent(Event<String> evt) {
        log.warn("SimpleHandler receive event's data : [{}]", evt.getData());
    }
    
    @Selector(value = "simple.string", reactor = "@reactor")
    public void handleString(String data) {
        log.warn("SimpleHandler receive data : [{}]", data);
    }

    @Selector(value = "java.lang.Exception", reactor = "@reactor", type = SelectorType.TYPE)
    public void handleException(Event<Exception> evt) {
        log.error("SimpleHandler receive exception:[{}]", evt.getData().getMessage());
    }

    @Selector(value = "/photo/{file}", reactor = "@reactor", type = SelectorType.URI)
    public void handleUri(Event<String> evt) {
        String file = evt.getHeaders().get("file");
        log.warn("SimpleHandler receive URI:[{} for {}]", evt.getData(), file);
    }

    @Selector(value = "09\\d{8}", reactor = "@reactor", type = SelectorType.REGEX)
    public void handleRegex(Event<String> evt) {
        log.warn("SimpleHandler receive mobile no : [{}]", evt.getData());
    }

    @Selector(value = "$.total", reactor = "@jsonPathReactor", type = SelectorType.JSON_PATH)
    public void handleJsonpath(Event<String> evt) {
        log.warn("SimpleHandler handle json-path:[{}]", evt.getData());
    }
}
