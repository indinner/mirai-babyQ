package com.example.miraihellp.service.MiraiHandlers;

import com.example.miraihellp.entity.BlackList;
import com.example.miraihellp.entity.GroupSetting;
import com.example.miraihellp.entity.KeyWord;
import com.example.miraihellp.server.catchServer.GroupServerCatch;
import com.example.miraihellp.server.catchServer.MongoTemplateCatch;
import com.example.miraihellp.service.SensitiveWordsFilter;
import com.example.miraihellp.utils.CreateImgUtil;
import lombok.extern.log4j.Log4j2;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.utils.ExternalResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author indinner
 * @Date 2023/6/8 14:03
 * @Version 1.0
 * @Doc:
 */
@Log4j2
@Component
public class GroupEventHandlers extends SimpleListenerHost {



    /**
     * 监听加群申请
     * @param event
     */
    @EventHandler
    public void joinGroup(MemberJoinRequestEvent event){
        if(!GroupServerCatch.groupSettingMap.containsKey(event.getGroupId())){
            return;
        }
        long fromId = event.getFromId();//获取申请人QQ号
        if(GroupServerCatch.blackList.containsKey(fromId)){
            //在黑名单
            event.reject(false,"已被拉黑,联系群主处理!");
        }else if(GroupServerCatch.groupSettingMap.get(event.getGroupId()).getJoinKey()){
            //如果开启了加群验证，判断用户发送的验证信息是否正确
            int lastColonIndex = event.getMessage().lastIndexOf("：");
            String answer=event.getMessage().substring(lastColonIndex + 1);
            if(GroupServerCatch.groupSettingMap.get(event.getGroupId()).getJoinAnswer().equals(answer)){
                //答案正确
                event.accept();
            }else {
                event.reject(false,"答案错误!");
            }

        }

    }



    /**
     * 监听关键词撤回
     * @param event
     */
    @EventHandler
    public void keyWord(GroupMessageEvent event){
        /*判断消息来源，如果不是成员消息，不处理*/
        if(event.getSender().getPermission() != MemberPermission.MEMBER){
            return;
        }
        SensitiveWordsFilter filter = new SensitiveWordsFilter(GroupServerCatch.keyWordList);
        if (GroupServerCatch.groupSettingMap.containsKey(event.getGroup().getId())) {
            GroupSetting groupSetting = GroupServerCatch.groupSettingMap.get(event.getGroup().getId());
            if(groupSetting.getKeyword()){
                //开启了关键词撤回功能
                if(filter.containsSensitiveWords(event.getMessage().contentToString())){
                    MessageSource.recall(event.getMessage());
                }
            }
        }
    }


    /**
     * 管理员指令
     */
    @EventHandler
    public void adminKey(GroupMessageEvent event) throws Exception {
        // 获取发送消息的成员
        Member sender = event.getSender();
        String key = event.getMessage().contentToString().substring(0, 1);
        ownerMessageHandle(key,event);
        // 判断是否为管理员
        if (sender.getPermission() == MemberPermission.OWNER) {
            // 群主
            log.info("群主消息");
            ownerMessageHandle(key,event);
            adminMessageHandle(key,event);

        } else if(sender.getPermission() == MemberPermission.ADMINISTRATOR){
            // 管理员
            log.info("管理员消息");
            adminMessageHandle(key,event);
        }else if(sender.getPermission()==MemberPermission.MEMBER){
            log.info("普通成员消息");
            memberMessageHandle(key,event);
        }

        //处理所有成员消息(群主,管理员,成员)
        allMemberMessageHandle(event);
    }

    /**
     * 处理群主指令
     * @param key
     * @param event
     */
    private void ownerMessageHandle(String key,GroupMessageEvent event){

    }

    /**
     * 处理管理员指令
     * @param key
     * @param event
     */
    private void adminMessageHandle(String key,GroupMessageEvent event){
        MessageChain messageChain=event.getMessage();
        List<Long> atList=new ArrayList<>();
        // 使用正则表达式提取QQ号码
        String content = messageChain.contentToString();
        Pattern qqPattern = Pattern.compile("\\d{5,12}");
        Matcher matcher = qqPattern.matcher(content);
        while (matcher.find()) {
            String qqString = matcher.group();
            long qq = Long.parseLong(qqString);
            atList.add(qq);
        }
        switch (key){
            case "踢":
                atList.forEach(qq->{
                    event.getGroup().getMembers().get(qq).kick("违规");//踢人
                });
                break;
            case "黑":
                atList.forEach(qq->{
                    BlackList blackList=new BlackList();
                    blackList.setQQ(qq);
                    MongoTemplateCatch.mongoTemplateTemp.save(blackList);
                    GroupServerCatch.blackList.put(qq,"new");
                    //mongoTemplate.save(blackList);//加入黑名单
                    event.getGroup().getMembers().get(qq).kick("违规");//踢人
                });
                break;
            case "+":
                String newKeyWord = event.getMessage().contentToString().substring(1);
                KeyWord keyWord=new KeyWord();
                keyWord.setContent(newKeyWord);
                keyWord.setState(1);
                MongoTemplateCatch.mongoTemplateTemp.save(keyWord);
                GroupServerCatch.keyWordList.add(newKeyWord);
                break;
            case "t":
                atList.forEach(qq->{
                    log.info(qq);
                    NormalMember member = event.getGroup().getMembers().get(qq);
                    member.setSpecialTitle("新人");
                });
        }
    }

    private void memberMessageHandle(String key,GroupMessageEvent event){

    }

    private void allMemberMessageHandle(GroupMessageEvent event) throws Exception {

        String msg=event.getMessage().contentToString();

        if(msg.substring(0,2).equals("喜报")){
            String text=msg.replace("喜报", "");
            xiBao(text,event);
        }

    }

    /*发送喜报*/
    private void xiBao(String msg,GroupMessageEvent event) throws Exception {
        // 加载BufferedImage
        BufferedImage bufferedImage = CreateImgUtil.createBufferImgByXiBao(msg);
        // 创建临时文件
        File tempFile = File.createTempFile("temp", ".png");
        // 将BufferedImage保存为临时文件
        ImageIO.write(bufferedImage, "png", tempFile);
        // 创建ExternalResource
        ExternalResource resource = ExternalResource.create(tempFile);
        // 上传图片到群中
        Image image = event.getGroup().uploadImage(resource);
        // 发送消息
        event.getGroup().sendMessage(image);
        // 删除临时文件
        tempFile.delete();
    }


}
