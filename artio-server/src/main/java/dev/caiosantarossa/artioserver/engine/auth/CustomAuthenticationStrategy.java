package dev.caiosantarossa.artioserver.engine.auth;

import dev.caiosantarossa.artioserver.engine.Gateway;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.real_logic.artio.decoder.AbstractLogonDecoder;
import uk.co.real_logic.artio.validation.AuthenticationStrategy;

public class CustomAuthenticationStrategy implements AuthenticationStrategy {

    private static final Logger LOGGER = LogManager.getLogger(Gateway.class);

    @Override
    public boolean authenticate(AbstractLogonDecoder logon) {

        LOGGER.info("Authenticating {}", logon);

        return true;
    }
}
