public class TokenBucketRateLimiter {
    private int maxTokens;
    private int tokens;
    private long lastRefillTime;
    private long refillInterval;
    private int tokensToAddPerInterval;

    public TokenBucketRateLimiter(int maxTokens, long refillInterval, int tokenRate) {
        this.maxTokens = maxTokens;
        this.refillInterval = refillInterval;
        this.tokens = maxTokens; // initially
        this.lastRefillTime = System.currentTimeMillis();
        this.tokensToAddPerInterval = tokenRate;
    }

    // limiter over accept request
    public synchronized boolean allowRequest(int tokensRequested) {
        refillToken();
        // valid condition
        if (tokens >= tokensRequested) {
            tokens -= tokensRequested;
            return true;
        }
        return false;
    }

    private void refillToken() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastRefillTime;
        int totalElapsedIntervals = (int) (elapsedTime / refillInterval);

        if (totalElapsedIntervals > 0) {
            tokens += totalElapsedIntervals * tokensToAddPerInterval;
            tokens = Math.min(maxTokens, tokens);
            lastRefillTime = currentTime;
        }
    }

    // simulate requests
    public static void main(String[] args) {
        int maxTokens = 10;
        long refillInterval = 1000; // every second.
        int tokensToAddPerInterval = 2; // rate = 2 tokens / interval

        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(maxTokens, refillInterval,
                tokensToAddPerInterval);

        int noOfRequests = 20;
        for (int i = 0; i < noOfRequests; ++i) {
            boolean allowed = rateLimiter.allowRequest(1);
            if (allowed) {
                System.out.println("request " + (i + 1) + " allowed!");
            } else {
                System.out.println("request " + (i + 1) + " DENIED!");
            }

            try {
                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}