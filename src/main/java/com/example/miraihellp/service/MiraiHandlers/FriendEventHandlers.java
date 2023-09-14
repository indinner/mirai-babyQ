package com.example.miraihellp.service.MiraiHandlers;

import com.example.miraihellp.entity.BlackList;
import com.example.miraihellp.server.catchServer.BabyQServerCatch;
import com.example.miraihellp.server.catchServer.GroupServerCatch;
import com.example.miraihellp.server.catchServer.MongoTemplateCatch;
import lombok.extern.log4j.Log4j2;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;

/**
 * @Author indinner
 * @Date 2023/6/8 15:31
 * @Version 1.0
 * @Doc:
 */
@Log4j2
public class FriendEventHandlers extends SimpleListenerHost {

    @EventHandler
    public void onMessage(FriendMessageEvent event) throws IOException {
        String key = event.getMessage().contentToString().substring(0, 2);
        switch (key){
            case "拉黑":
                ContactList<Group> babyQGroups = BabyQServerCatch.babyQ.getGroups();
                babyQGroups.forEach(group -> {
                    if(group.getBotPermission().getLevel()==1){
                        //如果bot在群里是管理员
                        String stringQQ=event.getMessage().contentToString().substring(2);
                        Long qq=Long.parseLong(stringQQ);
                        try {
                            BlackList blackList=new BlackList();
                            blackList.setQQ(qq);
                            addBlackList(blackList);
                            GroupServerCatch.blackList.put(qq,"new");
                            group.getMembers().get(qq).kick("违规，请联系管理员");
                        }catch (Exception e){
                            log.error("拉黑失败");
                        }
                    }
                });
        }
    }

    /**
     * 异步添加黑名单
     * @param blackList
     */
    @Async
    public void addBlackList(BlackList blackList){
        MongoTemplateCatch.mongoTemplateTemp.save(blackList);
    }


}
