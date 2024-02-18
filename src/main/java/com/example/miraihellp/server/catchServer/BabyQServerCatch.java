package com.example.miraihellp.server.catchServer;

import com.example.miraihellp.entity.GroupSetting;
import com.example.miraihellp.server.mirai.MiraiServer;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import net.mamoe.mirai.Bot;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Author indinner
 * @Date 2023/6/8 16:25
 * @Version 1.0
 * @Doc:
 */
@Component
@Log4j2
@EnableScheduling
public class BabyQServerCatch {


    public static Bot babyQ;



    /*加载babyQ机器人*/
    @PostConstruct
    private void initBabyQ(){
        babyQ = MiraiServer.login(1244018263);
    }

    /*每分钟检测一次bot是否在线*/
    @Scheduled(fixedRate = 60000) // 60,000毫秒等于1分钟
    public void executeTask() {
        if(!babyQ.isOnline()){
            log.error("bot离线,重新登陆");
            babyQ = MiraiServer.login(2270781775L);
        }else {
            log.info("bot当前状态正常");
        }
    }



}
