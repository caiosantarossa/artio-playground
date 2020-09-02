package dev.caiosantarossa.artioserver.library.handler;

import dev.caiosantarossa.artioserver.library.message.MessageEventProducerWithTranslator;
import dev.caiosantarossa.artioserver.library.session.LibrarySessions;
import io.aeron.logbuffer.ControlledFragmentHandler;
import org.agrona.DirectBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.real_logic.artio.library.FixLibrary;
import uk.co.real_logic.artio.library.LibraryConnectHandler;
import uk.co.real_logic.artio.library.OnMessageInfo;
import uk.co.real_logic.artio.library.SessionAcquireHandler;
import uk.co.real_logic.artio.library.SessionAcquiredInfo;
import uk.co.real_logic.artio.messages.DisconnectReason;
import uk.co.real_logic.artio.session.Session;
import uk.co.real_logic.artio.util.AsciiBuffer;
import uk.co.real_logic.artio.util.MutableAsciiBuffer;

import java.nio.ByteBuffer;

import static io.aeron.logbuffer.ControlledFragmentHandler.Action.CONTINUE;

public class SessionHandler implements uk.co.real_logic.artio.library.SessionHandler, LibraryConnectHandler, SessionAcquireHandler {

    private static final Logger LOGGER = LogManager.getLogger(SessionHandler.class);

    private final AsciiBuffer string = new MutableAsciiBuffer();

    private final MessageEventProducerWithTranslator producer;

    public SessionHandler(MessageEventProducerWithTranslator producer) {
        this.producer = producer;
    }

    public ControlledFragmentHandler.Action onMessage(
            final DirectBuffer buffer,
            final int offset,
            final int length,
            final int libraryId,
            final Session session,
            final int sequenceIndex,
            final long messageType,
            final long timestampInNs,
            final long position,
            final OnMessageInfo messageInfo) {
        string.wrap(buffer);

        LOGGER.info("{} -> MsgType: {}", session.id(), messageType);

        return CONTINUE;
    }

    public void onTimeout(final int libraryId, final Session session) {
    }

    public void onSlowStatus(final int libraryId, final Session session, final boolean hasBecomeSlow) {
    }

    public ControlledFragmentHandler.Action onDisconnect(final int libraryId, final Session session, final DisconnectReason reason) {
        LOGGER.info("{} Disconnected: {}", session.id(), reason);

        LibrarySessions.removeSession(session);

        return CONTINUE;
    }

    public void onSessionStart(final Session session) {
        LOGGER.info("onSessionStart: {}", session.id());
    }

    @Override
    public void onConnect(FixLibrary library) {
        LOGGER.info("Library Connected");
    }

    @Override
    public void onDisconnect(FixLibrary library) {
        LOGGER.info("Library Disconnected");
    }

    @Override
    public uk.co.real_logic.artio.library.SessionHandler onSessionAcquired(Session session, SessionAcquiredInfo acquiredInfo) {
        LOGGER.info("Session Acquired: {}", session.id());

        LibrarySessions.addSession(session);

        ByteBuffer byteBuffer = ByteBuffer.wrap(session.compositeKey().remoteCompId().getBytes());
        producer.onData(byteBuffer);

        return this;
    }
}
