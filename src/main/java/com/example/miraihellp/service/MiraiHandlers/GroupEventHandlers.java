package com.example.miraihellp.service.MiraiHandlers;

import com.example.miraihellp.entity.GroupSetting;
import com.example.miraihellp.server.catchServer.BabyQServerCatch;
import com.example.miraihellp.server.catchServer.GroupServerCatch;
import com.example.miraihellp.service.SensitiveWordsFilter;
import lombok.extern.log4j.Log4j2;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author indinner
 * @Date 2023/6/8 14:03
 * @Version 1.0
 * @Doc:
 */
@Log4j2
public class GroupEventHandlers extends SimpleListenerHost {



    /**
     * 监听关键词撤回
     * @param event
     */
    @EventHandler
    public void keyWord(GroupMessageEvent event){
        /*判断消息来源，如果不是成员消息，不处理*/
        if(event.getSender().getPermission() != MemberPermission.OWNER){
            return;
        }
        SensitiveWordsFilter filter = new SensitiveWordsFilter(GroupServerCatch.keyWordList);
        if (GroupServerCatch.groupSettingMap.containsKey(event.getGroup().getId())) {
            GroupSetting groupSetting = GroupServerCatch.groupSettingMap.get(event.getGroup().getId());
            if(groupSetting.getKeyword()){
                //开启了关键词撤回功能
                if(filter.containsSensitiveWords(event.getMessage().contentToString())){
                    MessageSource.recall(event.getMessage());
                }
            }
        }
    }


    /**
     * 管理员指令
     */
    @EventHandler
    public void adminKey(GroupMessageEvent event){
        // 获取发送消息的成员
        Member sender = event.getSender();

        // 判断是否为管理员
        if (sender.getPermission() == MemberPermission.OWNER) {
            // 群主
            log.info("群主消息");
            String key = event.getMessage().contentToString().substring(0, 1);
            ownerMessageHandle(key,event);
        } else if(sender.getPermission() == MemberPermission.ADMINISTRATOR){
            // 管理员
            log.info("管理员消息");
        }else {
            // 普通成员
            log.info("成员消息");
        }

    }

    /**
     * 处理群主消息
     * @param key
     * @param event
     */
    private void ownerMessageHandle(String key,GroupMessageEvent event){
        switch (key){
            case "踢":
                MessageChain messageChain=event.getMessage();

                List<Long> atList=new ArrayList<>();
                // 使用正则表达式提取QQ号码
                String content = messageChain.contentToString();
                Pattern qqPattern = Pattern.compile("\\d{5,12}");
                Matcher matcher = qqPattern.matcher(content);
                while (matcher.find()) {
                    String qqString = matcher.group();
                    long qq = Long.parseLong(qqString);
                    atList.add(qq);
                }
                atList.forEach(qq->{
                    event.getGroup().getMembers().get(qq).kick("违规");
                });
        }
    }


}
