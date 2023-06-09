package com.example.miraihellp.server.catchServer;

import com.example.miraihellp.entity.GroupSetting;
import com.example.miraihellp.entity.KeyWord;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author indinner
 * @Date 2023/6/8 18:07
 * @Version 1.0
 * @Doc:加载QQ群配置到系统内存
 */
@Component
public class GroupServerCatch {

    @Resource
    MongoTemplate mongoTemplate;

    //群组配置列表
    public static List<GroupSetting> groupSettingList=new ArrayList<>();

    //群组配置map，方便快速查找群组配置
    public static Map<Long,GroupSetting> groupSettingMap=new HashMap<>();

    //加载关键词
    public static List<String> keyWordList=new ArrayList<>();


    /**
     * 加载群聊设置
     */
    @PostConstruct
    private void initGroupSetting(){
        groupSettingList = mongoTemplate.findAll(GroupSetting.class);
        groupSettingList.forEach(groupSetting -> {
            groupSettingMap.put(groupSetting.getID(),groupSetting);
        });
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
    }



}
