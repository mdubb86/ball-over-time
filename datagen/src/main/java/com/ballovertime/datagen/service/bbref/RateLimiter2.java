//package com.ballovertime.datagen.service.bbref;
//
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.core.publisher.Sinks;
//
//import java.util.LinkedList;
//
//
//public class RateLimiter2 {
//
//    private final LinkedList<Integer> available;
//    private final LinkedList<Mono<RateLimiter.Token>> pending;
//
//    public RateLimiter2() {
//        available = new LinkedList<>();
//        pending = new LinkedList<>();
//
////        flux = Flux.create(emitter -> {
////            emitter.next(new Token(8));
////        }, FluxSink.OverflowStrategy.LATEST);
//    }
//
//    public synchronized Mono<Token> acquireToken() {
//        if (available.isEmpty()) {
//
//            Sinks.One<Integer> sink = Sinks.one();
//
//
//        } else {
//            return Mono.just(new Token(available.pop()));
//        }
//    }
//
//    public synchronized void releaseToken(int tokenId) {
//
//        if (pending.isEmpty()) {
//            available.push(tokenId);
//        } else {
//
//        }
//
//    }
//
//
//
//    private final record Token(int id, Runnable release) {};
//
//
//
//
//    public static void main(String[] args) {
//
//        RateLimiter2 rl = new RateLimiter2();
//
//
//        rl.
//
//    }
//
//}
