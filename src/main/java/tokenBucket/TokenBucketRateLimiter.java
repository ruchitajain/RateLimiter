package tokenBucket;

import java.util.Map;

public class TokenBucketRateLimiter {
    private final int refillRateS;
    private final double bucketSize;
    private long lastRefillTim;
    private double currTokenSize;
    TokenBucketRateLimiter(int bucketSize, int refillRateS ){
        this.refillRateS = refillRateS;
        this.bucketSize = bucketSize;
        currTokenSize = bucketSize;
        lastRefillTim =  System.nanoTime();
    }

    boolean rateLimit(){
       refill();
       if (currTokenSize >= 1){
           currTokenSize--;
           return true;
       }
       return false;
    }

    void refill(){
        long currTimeNano = System.nanoTime();
        long timeElapsedNano = currTimeNano - lastRefillTim;
        double elapsedSeconds = (timeElapsedNano /1_000_000_000.0);
        double refillToken = (elapsedSeconds * refillRateS);

        currTokenSize = Math.min(bucketSize,(currTokenSize+refillToken));
        lastRefillTim = currTimeNano;
    }

    public static void main(String [] args){
        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(3,3);
        for (int i=0; i<10; i++){
            System.out.println(rateLimiter.rateLimit());
        }
    }
}
