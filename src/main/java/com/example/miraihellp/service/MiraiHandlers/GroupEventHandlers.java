package com.example.miraihellp.service.MiraiHandlers;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import com.example.miraihellp.entity.BlackList;
import com.example.miraihellp.entity.GroupSetting;
import com.example.miraihellp.entity.KeyWord;
import com.example.miraihellp.entity.Points;
import com.example.miraihellp.server.catchServer.BabyQServerCatch;
import com.example.miraihellp.server.catchServer.GroupServerCatch;
import com.example.miraihellp.server.catchServer.MongoTemplateCatch;
import com.example.miraihellp.service.SensitiveWordsFilter;
import lombok.extern.log4j.Log4j2;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.utils.ExternalResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
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
    public void joinGroupRequest(MemberJoinRequestEvent event){
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
     * 监听加群信息
     * @param event
     */
    @EventHandler
    public void joinGroup(MemberJoinEvent event){
        long userID = event.getMember().getId();//获取进群人的QQ号
        if(GroupServerCatch.blackList.containsKey(userID)){
            //在黑名单
            event.getMember().kick("黑名单,联系520244处理");
        }
    }


    private static Map<Long,Integer> black_list_number=new HashMap<>();//记录触发关键词的次数

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
                    event.getSender().mute(60*60*24);
                    event.getGroup().sendMessage(event.getSender().getId()+"消息违规！");
                    BabyQServerCatch.babyQ.getFriend(520244L)
                            .sendMessage(event.getGroup().getName()+" 的 "+event.getSender().getId()+" 的消息："+
                                    event.getMessage().contentToString()+" 已撤回");
                }
                if(filter.containsSensitiveWords(event.getSender().getNameCard())){
                    MessageSource.recall(event.getMessage());
                    event.getSender().mute(60*60*24);
                    event.getGroup().sendMessage(event.getSender().getId()+"群昵称违规！");
                    BabyQServerCatch.babyQ.getFriend(520244L)
                            .sendMessage(event.getGroup().getName()+" 的 "+event.getSender().getId()+" 的消息："+
                                    event.getMessage().contentToString()+" 已撤回");
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
        String key2="--";
        if(event.getMessage().contentToString().length()>=2){
            key2 = event.getMessage().contentToString().substring(0, 2);
        }

        ownerMessageHandle(key,event);
        // 判断是否为管理员
        if (sender.getPermission() == MemberPermission.OWNER) {
            // 群主
            log.info("群主消息");
            ownerMessageHandle(key,event);
            adminMessageHandle(key,event);
            memberMessageHandle(key2,event);
        } else if(sender.getPermission() == MemberPermission.ADMINISTRATOR){
            // 管理员
            log.info("管理员消息");
            adminMessageHandle(key,event);
            memberMessageHandle(key2,event);
        }else if(sender.getPermission()==MemberPermission.MEMBER){
            log.info("普通成员消息");
            memberMessageHandle(key2,event);
        }
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
            /*case "1":
                ContactList<NormalMember> members = event.getGroup().getMembers();
                for (NormalMember member : members) {
                    member.sendMessage("锦绣校区起航驾校直招,驾校位置安建大南门对面200米，距离合师5分钟车程，VIP特快班特惠价2310!!!,联系微信：520244（本号不回复）");
                }*/
            case "踢":
                atList.forEach(qq->{
                    event.getGroup().getMembers().get(qq).kick("违规");//踢人
                });
                break;
            case "黑":
                atList.forEach(qq->{
                    BlackList blackList=new BlackList();
                    blackList.setQQ(qq);
                    addBlackList(blackList);
                    GroupServerCatch.blackList.put(qq,"new");
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
            case "-":
                String newKeyWord1 = event.getMessage().contentToString().substring(1);
                Query query=new Query(Criteria.where("content").is(newKeyWord1));
                MongoTemplateCatch.mongoTemplateTemp.remove(query, KeyWord.class);
                GroupServerCatch.keyWordList.remove(newKeyWord1);
                break;
        }
    }


    /**
     * 异步添加黑名单
     * @param blackList
     */
    @Async
    public void addBlackList(BlackList blackList){
        MongoTemplateCatch.mongoTemplateTemp.save(blackList);
    }



    /**
     * 处理普通成员消息
     * @param key
     * @param event
     */
    private void memberMessageHandle(String key,GroupMessageEvent event) throws Exception {
        Pattern p = Pattern.compile("[1-9][0-9]{4,9}");
        Matcher m = p.matcher(event.getMessage().contentToString());
        switch (key){
            case "禁言":
                System.out.println(event.getMessage().contentToString());
                Integer number=getLastNumber(event.getMessage().contentToString());//禁言时长
                /*查询积分*/
                Points points = MongoTemplateCatch.mongoTemplateTemp.findOne(new Query(Criteria.where("_id").is(String.valueOf(event.getSender().getId()))), Points.class);
                if(points!=null){
                    while(m.find()) {
                        String qqNumStr = m.group();
                        long qqNum = Long.parseLong(qqNumStr);
                        if(points.getPoints()-number>=0){
                            //禁言并扣除积分
                            NormalMember normalMember = event.getGroup().get(qqNum);
                            if (normalMember != null) {
                                normalMember.mute(number);
                                Update update=Update.update("points",points.getPoints()-number);
                                MongoTemplateCatch.mongoTemplateTemp
                                        .upsert(new Query(Criteria.where("_id").is(String.valueOf(event.getSender().getId()))),
                                                update,Points.class);
                            }
                        }
                    }
                }else {
                    event.getGroup().sendMessage("积分不足,可以通过[签到]获取");
                }
                break;
            case "签到":
                event.getSender().sendMessage("签你个头！");
                break;
            case "悲报":
                toImgApi("悲报",event.getMessage().contentToString().substring(2),event);
                break;
            case "喜报":
                toImgApi("喜报",event.getMessage().contentToString().substring(2),event);
                break;
            case "爱心":
                while(m.find()) {
                    String qqNumStr = m.group();
                    long qqNum = Long.parseLong(qqNumStr);
                    toImgApi("爱心",String.valueOf(qqNum),event);
                }
                break;
            case "屏幕":
                while(m.find()) {
                    String qqNumStr = m.group();
                    long qqNum = Long.parseLong(qqNumStr);
                    toImgApi("屏幕",String.valueOf(qqNum),event);
                }
                break;
            case "44":
                event.getGroup().sendMessage(to4());
                break;
        }
    }

    private String to4(){
        String res = HttpUtil.get("https://api.andeer.top/API/kfc.php");
        JSONObject jsonObject=new JSONObject(res);
        return jsonObject.getStr("data");
    }

    /*发送喜报*/
    private void toImgApi(String key,String msg,GroupMessageEvent event) throws Exception {

        String imageUrl="";
        switch (key){
            case "悲报":
                imageUrl="https://api.andeer.top/API/img_beibao.php?data="+msg;
                break;
            case "喜报":
                imageUrl="https://api.andeer.top/API/img_xibao.php?data="+msg;
                break;
            case "爱心":
                imageUrl="https://api.andeer.top/API/img_love.php?qq="+msg;
                break;
            case "屏幕":
                imageUrl="https://api.andeer.top/API/img_screen.php?qq="+msg;
                break;
            case "贴贴":
                imageUrl="https://api.andeer.top/API/gif_tietie.php?qq="+msg;
                break;
        }
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

    public Integer addPoints(String qq) {
        Query query = new Query(Criteria.where("_id").is(qq));

        Points points1 = MongoTemplateCatch.mongoTemplateTemp.findOne(query, Points.class);
        if(points1!=null){
            LocalDate date = Instant.ofEpochMilli(points1.getCreateTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate now = LocalDate.now();
            if(date.equals(now)){
                //已经签到
                return -1;
            }else {
                //没签到，签到
                Update update = new Update().inc("points", 1000);
                MongoTemplateCatch.mongoTemplateTemp.findAndModify(query, update, Points.class);
                return points1.getPoints()+1000;
            }
        }else {
            Points points = new Points(qq, 1000,new Date().getTime());
            MongoTemplateCatch.mongoTemplateTemp.insert(points);
            return 1000;
        }
    }

    public int getLastNumber(String str) {
        Pattern p = Pattern.compile("\\b\\d+$");
        Matcher m = p.matcher(str);
        if (m.find()) {
            String lastNumberStr = m.group();
            try {
                return Integer.parseInt(lastNumberStr);
            } catch (NumberFormatException e) {
                return -1;
            }
        } else {
            return -1;
        }
    }



}
