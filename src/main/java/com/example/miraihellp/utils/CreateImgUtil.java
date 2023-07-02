package com.example.miraihellp.utils;

import com.example.miraihellp.utils.CosBrowser.PutFile;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xhtmlrenderer.swing.Java2DRenderer;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * @Author indinner
 * @Date 2023/5/31 16:45
 * @Version 1.0
 * @Doc:
 */
@Service
@Log4j2
public class CreateImgUtil {

    /**
     * 通过类加载器的方式获取模板
     * springboot项目在部署的时候会打包成jar，打包成jar以后在使用freemaker时会出现以下报错：
     *           cannot be resolved to absolute file path because it does not reside in the file system: jar
     * 通过以下 setClassLoaderForTemplateLoading() 方法设置成类加载器的方式，可以解决上述无法访问模板路径的问题
     * @param template
     * @param map
     * @return
     * @throws IOException
     */
    private  static String getTemplateByClassLoader(String template, Map<String, Object> map) throws Exception {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);
        cfg.setClassLoaderForTemplateLoading(CreateImgUtil.class.getClassLoader(), "templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setClassicCompatible(true);
        Template temp = cfg.getTemplate(template);
        StringWriter stringWriter = new StringWriter();
        temp.process(map, stringWriter);
        stringWriter.flush();
        stringWriter.close();
        String result = stringWriter.getBuffer().toString();
        return result;
    }

    /**
     * ftl模板生成图片接口
     * @param template    ftl模板名称
     * @param map         模板占位符数据
     * @throws Exception
     */
    public static BufferedImage turnImage(String template, Map<String, Object> map,Integer width) throws Exception {
        String html =getTemplateByClassLoader(template, map);
        byte[] bytes = html.getBytes("UTF-8");

        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(bin);

        Java2DRenderer renderer = new Java2DRenderer(document, width, -1);
        return renderer.getImage();
        // 转成流上传至服务器
        //ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
        //ImageIO.write(img, "png", dataOutputStream);
        //byte[] bts = dataOutputStream.toByteArray();
        //return PutFile.upFile("ak-1302363069",bts, UUID.randomUUID().toString(),"wall_img","png");

    }

    /**
     * 处理过长的文本
     * @param input
     * @return
     */
    public List<String> splitString(String input, int number) {
        List<String> result = new ArrayList<>();
        int length = input.length();
        if (length <= number) {
            result.add(input);
        } else {
            for (int i = 0; i < length; i += number) {
                int endIndex = i + number;
                if (endIndex > length) {
                    endIndex = length;
                }
                result.add(input.substring(i, endIndex));
            }
        }
        return result;
    }

    /**
     * 获取一个喜报图片
     * @param text
     * @return
     * @throws Exception
     */
    public static BufferedImage createBufferImgByXiBao(String text) throws Exception {
        Map<String,Object> map=new HashMap<>();
        map.put("bgImg","https://cdn.indinner.com/babyQ/xibao1.png");
        map.put("text",text);
        return turnImage("jpg.ftl", map,800);
    }

}
