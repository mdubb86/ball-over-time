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
//import java.util.function.Supplier;
//
//
//public class ConcurrencyLimiter {
//
//    private final static Logger logger = LoggerFactory.getLogger(ConcurrencyLimiter.class);
//
//    private final LinkedList<Integer> available;
//    private final LinkedList<Sinks.One<Integer>> pending;
//
//    public ConcurrencyLimiter(int tokens) {
//        available = new LinkedList<>();
//        pending = new LinkedList<>();
//        for (int i=0; i<tokens; i++) {
//            available.push(i);
//        }
//    }
//
//    public synchronized <T> Mono<T> doInSlot(T target, SlottedHandler<T> handler) {
//        if (available.isEmpty()) {
//            Sinks.One<Integer> sink = Sinks.one();
//            pending.push(sink);
//            return sink.asMono()
//                    .flatMap(slot -> handler.process(target, slot).doAfterTerminate(() -> releaseSlot(slot)));
//        } else {
//            int slot = available.pop();
//            return handler.process(target, slot).doAfterTerminate(() -> releaseSlot(slot));
//        }
//    }
//
//    private synchronized void releaseSlot(int slot) {
//        if (pending.isEmpty()) {
//            available.push(slot);
//        } else {
//            pending.pop().tryEmitValue(slot);
//        }
//    }
//
//    @FunctionalInterface
//    public interface SlottedHandler<T> {
//        Mono<T> process(T target, int slot);
//    }
//
//    public static void main(String[] args) {
//        ConcurrencyLimiter rl = new ConcurrencyLimiter(2);
//
//        Flux.just("hello", "world", "goodbye")
//                .flatMap(str -> rl.doInSlot(str, (s, slot) -> {
//                    logger.info("Processing {} in slot {}", s, slot);
//                    return simulatedDelay(() -> s.toUpperCase());
//                }))
//                .doOnNext(res -> logger.info("Result: {}", res))
//                .blockLast();
//    }
//
//
//    public static <T> Mono<T> simulatedDelay(Supplier<T> obj) {
//        return Mono.just(obj.get())
//                .delayElement(Duration.ofMillis(ThreadLocalRandom.current().nextLong(1000, 5000)));
//    }
//}
