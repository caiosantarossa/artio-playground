package dev.caiosantarossa.artioserver.library.task;

import dev.caiosantarossa.artioserver.library.session.LibrarySessions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.real_logic.artio.builder.TestRequestEncoder;
import uk.co.real_logic.artio.session.Session;

import java.util.Objects;
import java.util.TimerTask;

public class SendMessageTask extends TimerTask {

    private static final Logger LOGGER = LogManager.getLogger(SendMessageTask.class);

    private final String targetCompId;

    public SendMessageTask(String targetCompId) {
        this.targetCompId = targetCompId;
    }

    @Override
    public void run() {
        LOGGER.info("Sending message to: {}", targetCompId);

        final TestRequestEncoder testRequest = new TestRequestEncoder();
        testRequest.testReqID("REQUEST." + System.currentTimeMillis());

        Session session = LibrarySessions.getSession(targetCompId);

        if (Objects.isNull(session)) {
            LOGGER.error("Session not found");
            return;
        }

        final long position = session.trySend(testRequest);
        LOGGER.info("Message sent: {}", position);
    }

}
