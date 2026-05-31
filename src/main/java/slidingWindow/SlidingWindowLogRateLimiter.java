package slidingWindow;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SlidingWindowLogRateLimiter {
    long windowMills;
    int maxReq;
    Map<String, Deque<Long>> userReqMap = new HashMap<>();

    public SlidingWindowLogRateLimiter(long windowDurationMillis, int maxRequests) {
        this.windowMills = windowDurationMillis;
        this.maxReq = maxRequests;
    }

    public boolean allow(String userId) {
        userReqMap.computeIfAbsent(userId, k-> new ConcurrentLinkedDeque<>());
        Deque<Long> reqLog = userReqMap.get(userId);
        synchronized (reqLog){
            long now = System.currentTimeMillis();
            long expireTime =  now - windowMills;
            while (!reqLog.isEmpty() && reqLog.peekFirst() <= expireTime){
                reqLog.pollFirst();
            }
            if (reqLog.size()>=maxReq){
                return false;
            }
            reqLog.offerLast(now);
            return true;
        }
    }

    public static void main(String[] args) {
        SlidingWindowLogRateLimiter limiter =
                new SlidingWindowLogRateLimiter(
                        10_000, // 10 seconds
                        3       // max 3 requests
                );

        System.out.println(limiter.allow("user1")); // true
        System.out.println(limiter.allow("user1")); // true
        System.out.println(limiter.allow("user1")); // true
        System.out.println(limiter.allow("user1")); // false
    }
}
