package dev.caiosantarossa.artioserver.engine;

import dev.caiosantarossa.artioserver.ThreadMonitor;
import dev.caiosantarossa.artioserver.engine.auth.CustomAuthenticationStrategy;
import dev.caiosantarossa.artioserver.library.MyLibrary;
import io.aeron.archive.Archive;
import io.aeron.archive.ArchiveThreadingMode;
import io.aeron.archive.ArchivingMediaDriver;
import io.aeron.driver.MediaDriver;
import org.agrona.IoUtil;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.SigInt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.real_logic.artio.CommonConfiguration;
import uk.co.real_logic.artio.engine.EngineConfiguration;
import uk.co.real_logic.artio.engine.FixEngine;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.aeron.driver.ThreadingMode.SHARED;

public class Gateway {

    private static final Logger LOGGER = LogManager.getLogger(Gateway.class);

    public static void main(final String[] args) throws InterruptedException {

        // Static configuration lasts the duration of a FIX-Gateway instance
        final String aeronChannel = "aeron:udp?endpoint=localhost:10000";

        final EngineConfiguration configuration = new EngineConfiguration()
                .bindTo("localhost", 9999)
                .libraryAeronChannel(aeronChannel);
        configuration.authenticationStrategy(new CustomAuthenticationStrategy());

        cleanupOldLogFileDir(configuration);

        final MediaDriver.Context context = new MediaDriver.Context()
                .threadingMode(SHARED)
                .dirDeleteOnStart(true);

        final Archive.Context archiveContext = new Archive.Context()
                .threadingMode(ArchiveThreadingMode.SHARED)
                .deleteArchiveOnStart(true);

        ThreadMonitor.startMonitor();

        LOGGER.info("Starting Gateway");

        try (ArchivingMediaDriver driver = ArchivingMediaDriver.launch(context, archiveContext);
             FixEngine gateway = FixEngine.launch(configuration)) {

            LOGGER.info("Gateway Started");

            runAgentUntilSignal(new MyLibrary());
        }

        System.exit(0);
    }

    public static void cleanupOldLogFileDir(final EngineConfiguration configuration) {
        IoUtil.delete(new File(configuration.logFileDir()), true);
    }

    public static void runAgentUntilSignal(final Agent agent) throws InterruptedException {
        final AgentRunner runner = new AgentRunner(
                CommonConfiguration.backoffIdleStrategy(),
                Throwable::printStackTrace,
                null,
                agent);

        final Thread thread = AgentRunner.startOnThread(runner);

        final AtomicBoolean running = new AtomicBoolean(true);
        SigInt.register(() -> running.set(false));

        while (running.get()) {
            Thread.sleep(100);
        }

        thread.join();
    }

}
