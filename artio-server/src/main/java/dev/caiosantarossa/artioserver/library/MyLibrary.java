package dev.caiosantarossa.artioserver.library;

import dev.caiosantarossa.artioserver.engine.Gateway;
import dev.caiosantarossa.artioserver.library.handler.SessionHandler;
import dev.caiosantarossa.artioserver.library.session.LibrarySessions;
import org.agrona.concurrent.Agent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.real_logic.artio.library.AcquiringSessionExistsHandler;
import uk.co.real_logic.artio.library.FixLibrary;
import uk.co.real_logic.artio.library.LibraryConfiguration;

import static java.util.Collections.singletonList;

public class MyLibrary implements Agent {

    private static final Logger LOGGER = LogManager.getLogger(Gateway.class);

    private static final int FRAGMENT_LIMIT = 10;

    final String aeronChannel = "aeron:udp?endpoint=localhost:10000";

    private FixLibrary library;

    @Override
    public void onStart() {
        final LibraryConfiguration configuration = new LibraryConfiguration();

        final SessionHandler sessionHandler = new SessionHandler();

        configuration
                .libraryConnectHandler(sessionHandler)
                .sessionAcquireHandler(sessionHandler)
                .sessionExistsHandler(new AcquiringSessionExistsHandler(false))
                .libraryAeronChannels(singletonList(aeronChannel));

        library = FixLibrary.connect(configuration);

        LOGGER.info("Connecting library");

        LibrarySessions.startMonitor();
    }

    @Override
    public int doWork() {
        return library.poll(FRAGMENT_LIMIT);
    }

    @Override
    public String roleName() {
        return "MyLibrary";
    }

}
