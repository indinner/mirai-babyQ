package com.example.miraihellp.service.MiraiHandlers;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageSource;

/**
 * @Author indinner
 * @Date 2023/6/8 15:31
 * @Version 1.0
 * @Doc:
 */
public class FriendEventHandlers extends SimpleListenerHost {

    @EventHandler
    public void onMessage(FriendMessageEvent event){
        System.out.println("**========================**");
        System.out.println("监听私聊消息事件:"+event.toString());
        System.out.println(event.getMessage().contentToString());
        if(event.getMessage() instanceof Image){
            Image image= (Image) event.getMessage();
            System.out.println(image.getMd5());
        }
    }

}
