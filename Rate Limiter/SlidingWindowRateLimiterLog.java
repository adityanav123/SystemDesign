import java.util.ArrayDeque;

public class SlidingWindowRateLimiterLog {
    private final int windowSize; // Size of the time window in milliseconds
    private final int maxRequests; // Maximum number of requests allowed in the window
    private final ArrayDeque<Long> requests; // Queue to hold timestamps of requests

    public SlidingWindowRateLimiterLog(int windowSize, int maxRequests) {
        this.windowSize = windowSize;
        this.maxRequests = maxRequests;
        this.requests = new ArrayDeque<>();
    }

    public synchronized boolean allowRequest() {
        long currentTime = System.currentTimeMillis();

        // Remove timestamps outside the window
        while (!requests.isEmpty() && currentTime - requests.peekFirst() >= windowSize) {
            requests.pollFirst();
        }

        if (requests.size() < maxRequests) {
            requests.offerLast(currentTime);
            return true;
        }

        return false;
    }

    public static void main(String[] args) {
        int windowSize = 5000; // 5 seconds window
        int maxRequests = 10; // Maximum of 10 requests within the window

        SlidingWindowRateLimiterLog rateLimiter = new SlidingWindowRateLimiterLog(windowSize, maxRequests);

        int noOfRequests = 50;
        for (int i = 0; i < noOfRequests; ++i) {
            boolean reqAllowed = rateLimiter.allowRequest();
            if (reqAllowed) {
                System.out.println("Request " + (i + 1) + " allowed!");
            } else {
                System.out.println("Request " + (i + 1) + " denied!");
            }
            try {
                Thread.sleep(100); // Simulate some delay between requests
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
