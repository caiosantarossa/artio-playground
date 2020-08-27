package dev.caiosantarossa.artioserver.library.session;

import org.agrona.collections.Object2ObjectHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.real_logic.artio.session.Session;

import java.util.Timer;
import java.util.TimerTask;

public class LibrarySessions {

    private static final Logger LOGGER = LogManager.getLogger(LibrarySessions.class);

    private static final Object2ObjectHashMap<String, Session> sessions = new Object2ObjectHashMap<>();

    public static void addSession(Session session) {
        sessions.put(session.compositeKey().remoteCompId(), session);
    }

    public static void removeSession(Session session) {
        sessions.remove(session.compositeKey().remoteCompId());
    }

    public static Session getSession(String targetCompId) {
        return sessions.get(targetCompId);
    }

    public static void startMonitor() {
        SessionMonitorTask monitorTask = new SessionMonitorTask();
        Timer t = new Timer();
        t.schedule(monitorTask, 1000, 2000);
    }

    static class SessionMonitorTask extends TimerTask {

        @Override
        public void run() {
            LOGGER.info("Total sessions: {}", sessions.size());
        }
    }
}
