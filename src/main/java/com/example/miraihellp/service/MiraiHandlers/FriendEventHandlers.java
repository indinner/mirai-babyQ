package com.example.miraihellp.service.MiraiHandlers;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.io.IOException;

/**
 * @Author indinner
 * @Date 2023/6/8 15:31
 * @Version 1.0
 * @Doc:
 */
public class FriendEventHandlers extends SimpleListenerHost {

    @EventHandler
    public void onMessage(FriendMessageEvent event) throws IOException {
        xiaoAi(event,event.getMessage().contentToString());
    }

    private void xiaoAi(FriendMessageEvent event, String question){
        JSONObject params=new JSONObject();
        params.set("user_text",question+"。注意：你的回答应该使用纯txt文本形式，不包括任何的markdown语法，换行或者斜杠等特殊字符");
        params.set("chat_id",event.getSender().getId());
        String result = HttpUtil.post("http://43.139.117.81:5001/gpt", params.toString());
        event.getFriend().sendMessage(result);
    }

    private void souImg(FriendMessageEvent event, String question) throws IOException {
        String url="http://xinnai.521314.love/API/b_sst.php?msg="+question;
        String imgUrl = HttpUtil.get(url);
        // 创建临时文件
        File tempFile = File.createTempFile("temp", ".png");
        HttpUtil.downloadFile(imgUrl, tempFile);
        // 创建ExternalResource
        ExternalResource resource = ExternalResource.create(tempFile);
        // 上传图片到群中
        Image image = event.getFriend().uploadImage(resource);
        // 发送消息
        event.getFriend().sendMessage(image);
        // 删除临时文件
        tempFile.delete();

    }

}
