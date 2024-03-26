package com.zy.elasticsearch.webmailelasticsearch.thread;

import com.google.common.util.concurrent.Callables;
import com.google.common.util.concurrent.Runnables;

import java.util.concurrent.*;

public class ThreadTest {
    public static ExecutorService executorService = Executors.newFixedThreadPool(10);
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        FutureTask task = new FutureTask<Integer>(new Callables());
////        Thread thread1 = new Thread(task);
////        thread1.start();
////        System.out.println("get:"+task.get());
////
////        Threads threads = new Threads();
////        threads.start();
////
////        Thread thread = new Thread(new Runnables());
////        thread.start();
//        executorService.execute(new Runnables());
//        for (int i =1;i<20;i++){
//            executorService.submit(new Runnables());
//        }
        completableFutureTest();
    }

    public static class Callables implements Callable{
        @Override
        public Object call() throws Exception {
            System.out.println("Callable");
            return 1;
        }
    }

    public static class Threads extends Thread {
        @Override
        public void run() {
            System.out.println("Thread");
        }
    }

    public static class Runnables implements Runnable{
        @Override
        public void run() {
            System.out.println("Runnable"+Thread.currentThread().getName());
        }
    }

    public static void completableFutureTest() throws ExecutionException, InterruptedException {
        //runAsync
//        CompletableFuture.runAsync(()->{
//            System.out.println("completableFutureTest");
//        },executorService);

        //supplyAsync 有返回值
//        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程:" + Thread.currentThread().getName());
//            return "completableFutureTest";
//        }, executorService).whenComplete((res,exception)->{
//            //whenComplete 当完成时需要的操作
//            System.out.println("whenComplete执行结束,结果是:"+res+",异常是:"+exception);
//        }).exceptionally(throwable -> {
//            //exceptionally 出现异常时的操作
//            return "异常返回消息";
//        });

        //supplyAsync 有返回值 handle 同时处理结果跟异常返回
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程:" + Thread.currentThread().getName());
            return "completableFutureTest";
        }, executorService).handle((res,exception)->{
            if (res != null){
                return "返回结果";
            }
            if (exception != null){
                return "返回异常";
            }
            return "0";
        });
        System.out.println(future.get());
    }
}

