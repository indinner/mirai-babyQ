package com.example.miraihellp.service.MiraiHandlers;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.example.miraihellp.entity.BlackList;
import com.example.miraihellp.entity.GroupSetting;
import com.example.miraihellp.entity.KeyWord;
import com.example.miraihellp.server.catchServer.BabyQServerCatch;
import com.example.miraihellp.server.catchServer.GroupServerCatch;
import com.example.miraihellp.server.catchServer.MongoTemplateCatch;
import com.example.miraihellp.service.SensitiveWordsFilter;
import com.example.miraihellp.utils.CreateImgUtil;
import lombok.extern.log4j.Log4j2;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import net.mamoe.mirai.message.action.BotNudge;
import net.mamoe.mirai.message.action.MemberNudge;
import net.mamoe.mirai.message.action.Nudge;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        //allMemberMessageHandle(event);
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

    /*处理普通成员消息*/
    private void allMemberMessageHandle(GroupMessageEvent event) throws Exception {




        /*if(event.getSender().getId()==520244&&event.getMessage().contentToString().equals("拉取")){
            //paiyipai(event);
            laqu(event);
            return;
        }*/

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

        if(event.getSender().getId()==520244&&event.getMessage().contentToString().substring(0,2).equals("拍")){
            paiyipai(event);
            //baozhao(event,atList.get(0));
            return;
        }

        Long qq = 0L;
        switch (event.getMessage().contentToString().substring(0,1)){
            case "撕":
            case "抱":
            case "咬":
            case "舔":
            case "玩":
            case "磕":
            case "吃":
            case "爬":
            case "写":
                if(atList.size()>0){
                    qq = atList.get(0);
                }
                siHead(qq,event,event.getMessage().contentToString().substring(0,1),event.getMessage().contentToString().replace("写",""));
                break;
            case "搜":
                souImg(event,event.getMessage().contentToString().substring(1));
        }
        if(atList.size()>0&&atList.get(0).equals(BabyQServerCatch.babyQ.getId())){
            //xiaoAi(event,event.getMessage().contentToString().substring(12));
        }

        if(event.getMessage().contentToString().substring(0,2).equals("喜报")){
            xiBao(event.getMessage().contentToString().replace("喜报",""),event);
        }

        if(event.getMessage().contentToString().substring(0,3).equals("抢龙王")&&event.getGroup().getId()==622878728){
            for (int i = 0; i < 300; i++) {
                yulu(event);
            }
        }
    }

    /*爆照复读机*/
    private void baozhao(GroupMessageEvent event,Long qq) throws InterruptedException {
        MessageChain chain = new MessageChainBuilder()
                .append(new At(qq))
                .append(new PlainText(" 爆照"))
                .build();

        for (int i = 0; i < 30; i++) {
            Thread.sleep(1000);
            event.getGroup().sendMessage(chain);
        }

    }

    /*拉取群成员资料*/
    private void laqu(GroupMessageEvent event){
        JSONArray jsonArray=new JSONArray();
        event.getGroup().getMembers().forEach(normalMember -> {
            Date date = new Date(normalMember.getJoinTimestamp() * 1000L);
            // 创建SimpleDateFormat对象，定义日期格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 使用SimpleDateFormat格式化Date对象为字符串
            String formattedDate = sdf.format(date);
            JSONObject jsonObject=new JSONObject();
            jsonObject.set("nickName",normalMember.getNick());
            jsonObject.set("qq",normalMember.getId());
            jsonObject.set("inGroupTime",formattedDate);
            jsonArray.add(jsonObject);
        });
        System.out.println(jsonArray);
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

    /*撕头像*/
    private void siHead(Long qq,GroupMessageEvent event,String key,String text) throws IOException {

        String URL = "http://8.130.26.128/API/si.php?QQ="+qq;
        switch (key){
            case "撕":
               URL="http://8.130.26.128/API/si.php?QQ="+qq;
               break;
            case "抱":
                URL="http://8.130.26.128/API/bao.php?qq="+qq;
                break;
            case "咬":
                URL="http://8.130.26.128/API/suck.php?QQ="+qq;
                break;
            case "舔":
                URL="http://8.130.26.128/API/tygo.php?qq="+qq;
                break;
            case "玩":
                URL="http://8.130.26.128/API/play.php?QQ="+qq;
                break;
            case "磕":
                URL="http://8.130.26.128/API/worsip.php?QQ="+qq;
                break;
            case "吃":
                URL="http://8.130.26.128/API/chi.php?QQ="+qq;
                break;
            case "爬":
                URL="http://8.130.26.128/API/pa.php?qq="+qq;
                break;
            case "写":
                URL="http://8.130.26.128/API/twihce.php?msg="+text;
        }
        String imageUrl=URL;
        // 创建临时文件
        File tempFile = File.createTempFile("temp", ".png");
        HttpUtil.downloadFile(imageUrl, tempFile);
        // 创建ExternalResource
        ExternalResource resource = ExternalResource.create(tempFile);
        // 上传图片到群中
        Image image = event.getGroup().uploadImage(resource);
        // 发送消息
        event.getGroup().sendMessage(image);
        // 删除临时文件
        tempFile.delete();
    }

    private void xiaoAi(GroupMessageEvent event,String question){
        JSONObject params=new JSONObject();
        params.set("user_text",question);
        params.set("chat_id",event.getSender().getId());
        String result = HttpUtil.post("http://43.139.117.81:5001/gpt", params.toString());
        log.info(result);
        event.getGroup().sendMessage(result);
    }

    /*搜图*/
    private void souImg(GroupMessageEvent event,String question) throws IOException {
        String url="http://xinnai.521314.love/API/b_sst.php?msg="+question;
        String imgUrl = HttpUtil.get(url);
        // 创建临时文件
        File tempFile = File.createTempFile("temp", ".png");
        HttpUtil.downloadFile(imgUrl, tempFile);
        // 创建ExternalResource
        ExternalResource resource = ExternalResource.create(tempFile);
        // 上传图片到群中
        Image image = event.getGroup().uploadImage(resource);
        // 发送消息
        event.getGroup().sendMessage(image);
        // 删除临时文件
        tempFile.delete();

    }

    /*随机语录*/
    private void yulu(GroupMessageEvent event){
        String url="http://xinnai.521314.love/API/juhe.php?msg=%E9%9A%8F%E6%9C%BA%E4%B8%80%E8%A8%80";
        String s = HttpUtil.get(url);
        event.getGroup().sendMessage(s);
    }

    /*拍一拍*/
    private void paiyipai(GroupMessageEvent event){
        //System.out.println(event.getGroup().getMembers().get(520244).nudge());

        ContactList<NormalMember> members = event.getGroup().getMembers();
        members.forEach(member->{
            member.nudge().sendTo(event.getGroup());
        });

        //event.getGroup().getMembers().get(520244).nudge().sendTo(event.getGroup());
    }

}
