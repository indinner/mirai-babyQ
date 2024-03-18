package com.example.miraihellp.server.catchServer;

import com.example.miraihellp.entity.BlackList;
import com.example.miraihellp.entity.GroupSetting;
import com.example.miraihellp.entity.KeyWord;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author indinner
 * @Date 2023/6/8 18:07
 * @Version 1.0
 * @Doc:加载QQ群配置到系统内存
 */
@Log4j2
@Component
public class GroupServerCatch {

    @Resource
    private MongoTemplate mongoTemplate;

    //群组配置列表
    public static List<GroupSetting> groupSettingList=new ArrayList<>();

    //群组配置map，方便快速查找群组配置
    public static Map<Long,GroupSetting> groupSettingMap=new HashMap<>();

    //加载关键词
    public static List<String> keyWordList=new ArrayList<>();

    //代课关键词
    public static List<String> dkKeyWordList=new ArrayList<>(Arrays.asList("代","课","代课","dk"));

    //加载黑名单map，方便快速查找黑名单内容
    public static Map<Long,String> blackList=new HashMap<>();

    public static MongoTemplate mongoTemplateTemp;


    /**
     * 加载群聊设置
     */
    @PostConstruct
    private void initGroupSetting(){
        groupSettingList = mongoTemplate.findAll(GroupSetting.class);
        groupSettingList.forEach(groupSetting -> {
            groupSettingMap.put(groupSetting.getID(),groupSetting);
        });
        log.info("群聊配置信息加载完毕");
    }

    /**
     * 加载关键词设置
     */
    @PostConstruct
    private void initKeyWordList(){
        List<KeyWord> keyWords = mongoTemplate.findAll(KeyWord.class);
        keyWords.forEach(keyWord -> {
            keyWordList.add(keyWord.getContent());
        });
        log.info("关键词配置加载完毕");
    }

    /**
     * 加载黑名单
     */
    @PostConstruct
    private void initBlackList(){
        List<BlackList> blackListList = mongoTemplate.findAll(BlackList.class);
        blackListList.forEach(blackList1 -> {
            blackList.put(blackList1.getQQ(), blackList1.getID());
        });
        log.info("黑名单加载完毕");
    }



}
