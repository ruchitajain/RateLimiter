package fixedWindow;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Data
class FixedWindow{
    long startId;
    int count;
}


public class FixedWindowRateLimiter {
    long windowDuration;
    int maxReq;
    Map<String, FixedWindow> fixedWindowMap = new ConcurrentHashMap<>();
    FixedWindowRateLimiter( long windowDuration, int maxReq){
        this.windowDuration = windowDuration;
        this.maxReq = maxReq;
    }
    public boolean allow(String userId) {
        long nowId = System.currentTimeMillis()/windowDuration;
        fixedWindowMap.putIfAbsent(userId,new FixedWindow(nowId,0));
        FixedWindow window = fixedWindowMap.get(userId);
        synchronized (window){
            if (nowId != window.startId){
                window.startId = nowId;
                window.count=0;
            }
            if (window.count>=maxReq){
                return false;
            }
            window.count++;
            return true;
        }
    }

    public static void main(String[] args) {

        FixedWindowRateLimiter limiter =
                new FixedWindowRateLimiter(10_000,3);

        System.out.println(limiter.allow("user1"));
        System.out.println(limiter.allow("user1"));
        System.out.println(limiter.allow("user1"));
        System.out.println(limiter.allow("user1"));
    }
}
