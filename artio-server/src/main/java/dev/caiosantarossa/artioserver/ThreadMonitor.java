package dev.caiosantarossa.artioserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class ThreadMonitor {

    private static final Logger LOGGER = LogManager.getLogger(ThreadMonitor.class);

    public static void startMonitor() {
        ThreadMonitorTask monitorTask = new ThreadMonitorTask();
        Timer t = new Timer();
        t.schedule(monitorTask, 10000, 5000);
    }

    static class ThreadMonitorTask extends TimerTask {

        @Override
        public void run() {
            Set<String> threads = Thread.getAllStackTraces().keySet().stream()
                    .map(Thread::getName).collect(Collectors.toSet());

            LOGGER.info("Total threads: {}. {}", threads.size(),
                    threads.stream().collect(Collectors.joining("#")));
        }
    }


}
