package com.example.miraihellp;

import com.example.miraihellp.entity.BlackList;
import com.example.miraihellp.entity.GroupSetting;
import com.example.miraihellp.entity.KeyWord;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest
class MiraiHellpApplicationTests {

    @Test
    void contextLoads() {
    }


    @Resource
    MongoTemplate mongoTemplate;

    /**
     * 添加群设置
     */
    //@Test
    void addGroupSetting(){
        GroupSetting groupSetting=new GroupSetting();
        groupSetting.setID(644687732L);
        groupSetting.setTwoClass(true);
        groupSetting.setKeyword(true);
        groupSetting.setJoinKey(false);
        groupSetting.setJoinAnswer("null");
        mongoTemplate.save(groupSetting);
    }

    /**
     * 设置撤回关键词
     */
    void addKeyWord(){
        KeyWord keyWord=new KeyWord();
        keyWord.setContent("勤工俭学");
        keyWord.setState(0);
        mongoTemplate.save(keyWord);
    }

    /**
     * 设置黑名单
     */
    void addBlackList(){
        BlackList blackList=new BlackList();
        blackList.setQQ(11111L);
        mongoTemplate.save(blackList);
    }

}
