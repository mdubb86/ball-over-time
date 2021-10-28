//package com.ballovertime.datagen.service.bbref;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.core.publisher.Sinks;
//
//import java.time.Duration;
//import java.util.LinkedList;
//import java.util.concurrent.ThreadLocalRandom;
//import java.util.function.Function;
//import java.util.function.Supplier;
//
//
//public class RateLimiter {
//
//    private final static Logger logger = LoggerFactory.getLogger(RateLimiter.class);
//
//    private int available;
//    private final LinkedList<Sinks.One<Integer>> pending;
//
//    public RateLimiter(int tokens) {
//        available = tokens;
//        pending = new LinkedList<>();
//    }
//
//    public synchronized <T> Mono<T> limit(Function<T, Mono<T>> function, T target) {
//        if (available == 0) {
//            Sinks.One<Integer> sink = Sinks.one();
//            pending.add(sink);
//            return sink.asMono()
//                    .flatMap(a -> function.apply(target)
//                            .delayElement(Duration.ofMillis(3000))
//                            .doAfterTerminate(this::release));
//        } else {
//            available--;
//            return function.apply(target).doAfterTerminate(this::release);
//        }
//    }
//
//    private synchronized void release() {
//        if (pending.isEmpty()) {
//            available++;
//        } else {
//            pending.pop().tryEmitValue(available);
//        }
//    }
//
//    public static void main(String[] args) {
//        RateLimiter rl = new RateLimiter(2);
//
//        Flux.range(0, 10)
//                .flatMap(val -> rl.limit(RateLimiter::doExpensiveAsyncThing, val))
//                .doOnNext(res -> logger.info("Result: {}", res))
//                .blockLast();
//    }
//
//    public static Mono<Integer> doExpensiveAsyncThing(Integer val) {
//        return simulatedDelay(() -> val * val, 1000, 2000);
//    }
//
//    public static <T> Mono<T> simulatedDelay(Supplier<T> obj, long lowerBound, long upperBound) {
//        return Mono.just(obj.get())
//                .delayElement(Duration.ofMillis(ThreadLocalRandom.current().nextLong(lowerBound, upperBound)));
//    }
//}
