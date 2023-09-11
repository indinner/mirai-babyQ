package com.example.miraihellp.server.mirai;

import com.example.miraihellp.service.MiraiHandlers.FriendEventHandlers;
import com.example.miraihellp.service.MiraiHandlers.GroupEventHandlers;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.utils.BotConfiguration;

public class MiraiServer {
    public static void main(String[] args) {

        // 使用自定义配置
        Bot bot = BotFactory.INSTANCE.newBot(1244018263, BotAuthorization.byQRCode(), new BotConfiguration() {{
            fileBasedDeviceInfo(); // 使用 device.json 存储设备信息
            setProtocol(MiraiProtocol.MACOS); // 切换协议
        }});
        bot.getConfiguration().enableContactCache();//开启列表缓存，正式上线建议关闭
        bot.login();
        MiraiServer.afterLogin(bot);
    }



    public static Bot login(long qq){
        // 使用自定义配置
        Bot bot = BotFactory.INSTANCE.newBot(qq, BotAuthorization.byQRCode(), new BotConfiguration() {{
            fileBasedDeviceInfo(); // 使用 device.json 存储设备信息
            setProtocol(MiraiProtocol.ANDROID_WATCH); // 切换协议
        }});
        //bot.getConfiguration().enableContactCache();//开启列表缓存，正式上线建议关闭
        bot.login();
        System.out.println("陌生人数量"+bot.getStrangers().size());
        MiraiServer.afterLogin(bot);
        return bot;
    }


    public static void afterLogin(Bot bot) {
        bot.getEventChannel().registerListenerHost(new GroupEventHandlers());//注册自定义群聊监听事件
        bot.getEventChannel().registerListenerHost(new FriendEventHandlers());//注册自定义私聊事件
    }
}

