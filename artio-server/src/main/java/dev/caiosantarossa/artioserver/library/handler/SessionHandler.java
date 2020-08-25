package dev.caiosantarossa.artioserver.library.handler;

import dev.caiosantarossa.artioserver.engine.Gateway;
import io.aeron.logbuffer.ControlledFragmentHandler;
import org.agrona.DirectBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.real_logic.artio.builder.Printer;
import uk.co.real_logic.artio.decoder.PrinterImpl;
import uk.co.real_logic.artio.library.OnMessageInfo;
import uk.co.real_logic.artio.messages.DisconnectReason;
import uk.co.real_logic.artio.session.Session;
import uk.co.real_logic.artio.util.AsciiBuffer;
import uk.co.real_logic.artio.util.MutableAsciiBuffer;

import static io.aeron.logbuffer.ControlledFragmentHandler.Action.CONTINUE;

public class SessionHandler implements uk.co.real_logic.artio.library.SessionHandler {

    private static final Logger LOGGER = LogManager.getLogger(Gateway.class);

    private final AsciiBuffer string = new MutableAsciiBuffer();
    private final Printer printer = new PrinterImpl();

    public SessionHandler(final Session session) {
        LOGGER.info(session.compositeKey() + " logged in");
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

        try {
            LOGGER.info("{} -> {}", session.id(), printer.toString(string, offset, length, messageType));
        } catch (Exception e) {
            LOGGER.info("{} -> {}", session.id(), messageType);
        }

        return CONTINUE;
    }

    public void onTimeout(final int libraryId, final Session session) {
    }

    public void onSlowStatus(final int libraryId, final Session session, final boolean hasBecomeSlow) {
    }

    public ControlledFragmentHandler.Action onDisconnect(final int libraryId, final Session session, final DisconnectReason reason) {
        LOGGER.info("{} Disconnected: {}", session.id(), reason);
        return CONTINUE;
    }

    public void onSessionStart(final Session session) {
    }

}
