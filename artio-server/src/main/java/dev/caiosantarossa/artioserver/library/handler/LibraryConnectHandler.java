package dev.caiosantarossa.artioserver.library.handler;

import dev.caiosantarossa.artioserver.engine.Gateway;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.real_logic.artio.library.FixLibrary;

public class LibraryConnectHandler implements uk.co.real_logic.artio.library.LibraryConnectHandler {

    private static final Logger LOGGER = LogManager.getLogger(Gateway.class);

    public void onConnect(final FixLibrary library) {
        LOGGER.info("Library Connected");
    }

    public void onDisconnect(final FixLibrary library) {
        LOGGER.info("Library Disconnected");
    }
}
