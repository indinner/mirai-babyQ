package com.example.miraihellp.service;

import io.github.asleepyfish.config.ChatGPTProperties;
import io.github.asleepyfish.service.OpenAiProxyService;

/**
 * @Author indinner
 * @Date 2023/11/23 22:01
 * @Version 1.0
 * @Doc: Gpt工厂类,提供Gpt实例
 */
public class GPT {

    private static String baseUrl="https://service-3jw985st-1310173412.ca.apigw.tencentcs.com/release/";

    public static OpenAiProxyService GPT4;


    static {
        ChatGPTProperties properties = ChatGPTProperties.builder()
                .baseUrl(baseUrl)
                .token("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")
                .model("gpt-3.5-turbo")
                .chatModel("gpt-4-1106-preview")
                .build();
        GPT4=new OpenAiProxyService(properties);
    }




}
