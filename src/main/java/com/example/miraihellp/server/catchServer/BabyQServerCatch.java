package com.example.miraihellp.server.catchServer;

import com.example.miraihellp.entity.GroupSetting;
import com.example.miraihellp.server.mirai.MiraiServer;

import jakarta.annotation.PostConstruct;
import net.mamoe.mirai.Bot;
import org.springframework.data.mongodb.core.MongoTemplate;
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
public class BabyQServerCatch {


    public static Bot babyQ;



    /*加载babyQ机器人*/
    @PostConstruct
    private void initBabyQ(){
        babyQ = MiraiServer.login(3239535236L);
    }



}
