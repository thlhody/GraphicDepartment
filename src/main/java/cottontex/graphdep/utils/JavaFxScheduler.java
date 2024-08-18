package cottontex.graphdep.utils;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;

public class JavaFxScheduler {
    private static final Scheduler INSTANCE = Schedulers.from(Platform::runLater);

    public static Scheduler platform() {
        return INSTANCE;
    }
}