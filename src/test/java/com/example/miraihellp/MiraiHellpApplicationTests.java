package com.example.miraihellp;

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
    void addGroupSetting(){
        GroupSetting groupSetting=new GroupSetting();
        groupSetting.setID(389015312L);
        groupSetting.setTwoClass(true);
        groupSetting.setKeyword(true);
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

}
