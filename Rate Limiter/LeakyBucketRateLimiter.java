import java.util.ArrayDeque;

public class LeakyBucketRateLimiter {
    private ArrayDeque<Long> rateLimitingQueue;
    private int leakRate;
    private int capacity;
    private long lastLeakTime = System.currentTimeMillis();

    public LeakyBucketRateLimiter(int leakRate, int capacity) {
        rateLimitingQueue = new ArrayDeque<>();
        this.leakRate = leakRate;
        this.capacity = capacity;
    }

    public synchronized boolean acceptRequest() {
        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime - lastLeakTime;
        int leaks = (int) (timeElapsed / 1000) * leakRate; // calculate number of leaks since last update

        while (!rateLimitingQueue.isEmpty() && leaks > 0) {
            rateLimitingQueue.pollFirst();
            leaks--;
        }

        if (rateLimitingQueue.size() < capacity) {
            rateLimitingQueue.offerLast(currentTime);
            lastLeakTime = currentTime;
            return true;
        }

        return false;
    }

    public static void main(String[] args) {
        int leakRate = 5; // 5 requests per second
        int capacity = 10;

        LeakyBucketRateLimiter rateLimiter = new LeakyBucketRateLimiter(leakRate, capacity);

        // simulate client requests
        int noOfRequests = 20;
        for (int i = 0; i < noOfRequests; ++i) {
            boolean reqAccepted = rateLimiter.acceptRequest();
            if (reqAccepted) {
                System.out.println("Request " + (i + 1) + " accepted!");
            } else {
                System.out.println("Request " + (i + 1) + " denied!");
            }
            try {
                Thread.sleep(200); // Simulate some delay between requests
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
