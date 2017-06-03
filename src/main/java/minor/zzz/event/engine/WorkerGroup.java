package minor.zzz.event.engine;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhouzb on 2017/5/24.
 */
public class WorkerGroup {

    private AtomicInteger counter = new AtomicInteger(0);
    private Worker[] workers;

    public WorkerGroup(int workerCount) {
        workerCount = workerCount <= 0 ? 1 : workerCount;

        this.workers = new Worker[workerCount];

        for (int i = 0; i < workerCount; i ++) {
            this.workers[i] = new Worker();
        }
    }

    public Worker next() {
        int count = counter.getAndAdd(1);
        return this.workers[count % this.workers.length];
    }
}
