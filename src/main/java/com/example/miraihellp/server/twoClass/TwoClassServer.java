package com.example.miraihellp.server.twoClass;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import com.example.miraihellp.server.catchServer.BabyQServerCatch;
import com.example.miraihellp.server.catchServer.GroupServerCatch;
import com.example.miraihellp.utils.AESDecryptor;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author indinner
 * @Date 2023/6/5 16:38
 * @Version 1.0
 * @Doc:第二课堂相关服务
 */
@Service
@EnableScheduling
@Log4j2
public class TwoClassServer {

    private static String AES_KEY="Hfnuk#Client2022";

    private static String TOKEN="tdpW289Ops/ZjCLrVb3585H48sein494hRDqSFPtbHEqrIVvbjDxylUSdpMdUyUsmq6eZgMeIoHmA2DyHIClM5ePwhAuZpXmfc4SAGw4i1SOr2AagaraVXBpc3ZW/74cGiK1xexJhbd19RHlnNh4FA==";

    private static String DEVICE="FAAE6E21-013F-42D1-B722-599DE3DC340F";

    private static String ACVITITY_URL="http://ekta.hfnu.edu.cn/#/ActivityShow/";

    private Integer ACVITITY_ID=0;


    /*获取活动列表*/
    public JSONArray getActivityList(JSONObject params,JSONObject authorization) throws Exception {

        String URL="http://ekta.hfnu.edu.cn/api/app/client/v1/page/activity/list?params=";

        /*对参数进行加密*/
        String params_= AESDecryptor.encryptAES(params.toString(),AES_KEY);
        String authorization_=AESDecryptor.encryptAES(authorization.toString(), AES_KEY);

        /*对params进行url编码*/
        params_= URLEncoder.encode(params_,"UTF-8");

        URL=URL+params_;

        HttpResponse httpResponse = HttpRequest.get(URL)
                .header("Authorization", authorization_)
                .header("Accept-Encoding", "gzip, deflate")
                .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 15_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/8.0.34(0x18002234) NetType/4G Language/zh_CN")
                .header("Accept-Language", "zh-CN,zh-Hans;q=0.9")
                .execute();
        String body=AESDecryptor.decryptAES(httpResponse.body(),AES_KEY);
        JSONObject resultData=new JSONObject(body);
        JSONObject data=resultData.getJSONObject("data");
        JSONArray list=data.getJSONArray("list");
        return list;

    }

    //获取最新的活动并通知
    public List<String> getNewActivity() throws Exception {
        JSONArray allArray=new JSONArray();
        for (int i = 0; i < 10; i++) {
            //构建查询参数
            JSONObject params=new JSONObject();
            params.set("search","");
            params.set("pageNum",i+1);
            params.set("pageSize",10);
            params.set("sortType",1);
            params.set("activityTime",this.LocalDateTime());
            params.set("classifyId",0);

            JSONObject authorization=new JSONObject();
            authorization.set("token",TOKEN);
            authorization.set("platform",3);
            authorization.set("version","2.0.5");
            authorization.set("device",DEVICE);
            authorization.set("timestamp",new Date().getTime());

            JSONArray activityList = this.getActivityList(params, authorization);
            if(activityList.size()<10){
                allArray.addAll(activityList);
                break;
            }else {
                allArray.addAll(activityList);
            }
        }

        List<JSONObject> jsonObjectList = allArray.stream()
                .map(obj -> (JSONObject) obj)
                .collect(Collectors.toList());
        List<String> res=new ArrayList<>();
        for (int i = 0; i < jsonObjectList.size(); i++) {
            String name=jsonObjectList.get(i).getStr("name");
            Integer people=jsonObjectList.get(i).getInt("peopleLimit")-jsonObjectList.get(i).getInt("enrollCount");
            Integer id=jsonObjectList.get(i).getInt("id");
            String text="活动名称："+name+"\n"+"剩余名额："+people+"\n"+"直达链接："+ACVITITY_URL+id;
            res.add(text);
        }
        return res;
    }


    @Scheduled(fixedRate = 42000)
    public String showActivity() throws Exception {
        //构建查询参数
        JSONObject params=new JSONObject();
        params.set("search","");
        params.set("pageNum",1);
        params.set("pageSize",10);
        params.set("sortType",1);
        params.set("activityTime",this.LocalDateTime());
        params.set("classifyId",0);


        JSONObject authorization=new JSONObject();
        authorization.set("token",TOKEN);
        authorization.set("platform",3);
        authorization.set("version","2.0.5");
        authorization.set("device",DEVICE);
        authorization.set("timestamp",new Date().getTime());

        JSONArray activityList = this.getActivityList(params, authorization);

        JSONObject activity= (JSONObject) activityList.get(0);

        //读取活动id
        Integer id=activity.getInt("id");

        log.info("获取的最新活动ID如下:{}",id);
        log.info("当前记录的活动ID如下:{}",ACVITITY_ID);
        if(ACVITITY_ID == 0){
            ACVITITY_ID=id;
            return null;
        }else if(ACVITITY_ID.equals(id)){
            return null;
        }else {
            /*
            * TODO:发送群聊通知*/
            if(GroupServerCatch.groupSettingList.size()>0){
                GroupServerCatch.groupSettingList.forEach(groupSetting -> {
                    if(groupSetting.getTwoClass()){
                        String msg="新活动:"+activity.getStr("name")+"\n点击链接直达~\n"+ACVITITY_URL+id;
                        BabyQServerCatch.babyQ.getGroup(groupSetting.getID()).sendMessage(msg);
                    }
                });
            }


            ACVITITY_ID=id;
        }
        return null;

    }

    public static void main(String[] args) throws Exception {
        TwoClassServer twoClassService=new TwoClassServer();
        //twoClassService.showActivity();
        twoClassService.getNewActivity();

    }

    private Long LocalDateTime(){
        LocalDateTime now = LocalDateTime.now();
        // 将时间设置为0点
        LocalDateTime zero = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        // 将时间转换为时间戳
        long timestamp = zero.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        return timestamp;
    }

}
