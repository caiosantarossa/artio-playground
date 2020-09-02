package dev.caiosantarossa.artioserver.library.message;

import com.lmax.disruptor.EventHandler;
import dev.caiosantarossa.artioserver.library.session.LibrarySessions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.real_logic.artio.builder.TestRequestEncoder;
import uk.co.real_logic.artio.session.Session;

import java.util.Objects;

public class MessageEventHandler implements EventHandler<MessageEvent> {

    private static final Logger LOGGER = LogManager.getLogger(MessageEventHandler.class);

    @Override
    public void onEvent(MessageEvent event, long sequence, boolean endOfBatch) throws InterruptedException {
        LOGGER.info("Sending message to: {}", event.targetCompId);

        final TestRequestEncoder testRequest = new TestRequestEncoder();
        testRequest.testReqID("REQUEST." + System.currentTimeMillis());
        testRequest.header().targetCompID(event.targetCompId);

        Session session = LibrarySessions.getSession(event.targetCompId);

        if (Objects.isNull(session)) {
            LOGGER.error("Session not found");
            return;
        }

        final long position = session.trySend(testRequest);
        LOGGER.info("Message sent: {}", position);
    }

}
