package com.example.miraihellp.server.catchServer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author indinner
 * @Date 2023/6/9 22:17
 * @Version 1.0
 * @Doc:mongotemplate在handle中会出现注入失败，故在这里提供一个单例
 */
@Log4j2
@Component
public class MongoTemplateCatch {

    public static MongoTemplate mongoTemplateTemp;

    @Resource
    MongoTemplate mongoTemplate;

    @PostConstruct
    private void initMongoTemplate(){
        mongoTemplateTemp=mongoTemplate;
    }

}
