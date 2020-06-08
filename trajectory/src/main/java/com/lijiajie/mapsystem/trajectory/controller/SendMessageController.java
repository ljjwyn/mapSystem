//package com.lijiajie.mapsystem.trajectory.controller;
//
//import com.alibaba.fastjson.JSONObject;
//import com.lijiajie.mapsystem.trajectory.rabbitMq.LocationMessage;
//import com.lijiajie.mapsystem.trajectory.rabbitMq.producer.LocationProducer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.concurrent.ListenableFutureCallback;
//import org.springframework.web.bind.annotation.*;
//import java.util.Map;
//import java.util.concurrent.CountDownLatch;
//
///**
// * @CreateTime : 2020/5/16
// * @Description :
// **/
//@RestController
//public class SendMessageController {
//    private Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Autowired
//    RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法
//
//    @Autowired
//    LocationProducer locationProducer;
//
//    @GetMapping("/testSendMessage")
//    @ResponseBody
//    public String sendDirectMessage() throws InterruptedException {
//        LocationMessage locationMessage = new LocationMessage();
//        locationMessage.setAltitude("100");
//        locationMessage.setUserId("21180231335");
//        locationMessage.setLatitude("36.17064");
//        locationMessage.setLongitude("120.506723");
//        locationMessage.setDescribe("school");
//        locationProducer.asyncSend(locationMessage).addCallback(new ListenableFutureCallback<Void>() {
//
//            @Override
//            public void onFailure(Throwable e) {
//                logger.info("[testASyncSend][发送编号：[{}] 发送异常]]", locationMessage.getUserId(), e);
//            }
//
//            @Override
//            public void onSuccess(Void aVoid) {
//                logger.info("[testASyncSend][发送编号：[{}] 发送成功，发送成功]", locationMessage.getUserId());
//            }
//        });
//        logger.info("[testASyncSend][线程编号:{},发送编号：[{}] 调用完成]",Thread.currentThread().getId(), locationMessage.getUserId());
////        locationProducer.syncSend(locationMessage);
////        logger.info("[testSyncSend][发送编号：[{}] 发送成功]", locationMessage.getUserId());
//        // 阻塞等待，保证消费
//        // new CountDownLatch(1).await();
//        return "ok";
//    }
//
//    @RequestMapping(value = "/postlocations", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.POST)
//    @ResponseBody
//    public String getLoc(@RequestBody Map<String, String> resquestParams){
//        JSONObject result = new JSONObject();
//        LocationMessage locationMessage = new LocationMessage();
//        locationMessage.setAltitude(resquestParams.get("altitude"));
//        locationMessage.setUserId(resquestParams.get("userId"));
//        locationMessage.setLatitude(resquestParams.get("latitude"));
//        locationMessage.setLongitude(resquestParams.get("longitude"));
//        locationMessage.setDescribe(resquestParams.get("describe"));
//        locationProducer.asyncSend(locationMessage).addCallback(new ListenableFutureCallback<Void>() {
//
//            @Override
//            public void onFailure(Throwable e) {
//                logger.info("[testASyncSend][发送编号：[{}] 发送异常]]", resquestParams.get("userId"), e);
//                result.put("state","error");
//                result.put("message","发送异常");
//            }
//
//            @Override
//            public void onSuccess(Void aVoid) {
//                logger.info("[testASyncSend][发送编号：[{}] 发送成功，发送成功]", resquestParams.get("userId"));
//                result.put("state","success");
//                result.put("message","发送成功");
//            }
//        });
//        logger.info("[testASyncSend][发送编号：[{}] 调用完成]", resquestParams.get("userId"));
//        return result.toJSONString();
//    }
//
//
//}