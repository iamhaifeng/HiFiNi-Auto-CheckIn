package cloud.ohiyou.service.push.impl;

import cloud.ohiyou.config.EnvConfig;
import cloud.ohiyou.constant.PushPlatform;
import cloud.ohiyou.service.push.AbstractPushStrategy;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Bark推送策略
 *
 * @author ohiyou
 */
public class BarkPushStrategy extends AbstractPushStrategy {

    public BarkPushStrategy(OkHttpClient client) {
        // 假设您的 PushPlatform 枚举中已经定义了 BARK，如果没有，请先去该枚举中添加
        super(client, PushPlatform.BARK);
    }

    @Override
    protected void doPush(String title, String message) throws Exception {
        // 从配置中获取 Bark 的基础 URL 和设备码 (Device Key)
        String barkUrl = EnvConfig.get().getBarkUrl(); // 示例: https://api.day.app
        String barkDeviceKey = EnvConfig.get().getBarkDeviceKey(); // 示例: dw22pb5r95BseuyFNP9q9j

        // 对标题和内容进行 URL 编码，防止特殊字符或中文破坏 URL 结构
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());

        // 拼接 Bark 标准的 URL 格式: base_url/device_key/title/message
        // 确保 URL 末尾和各段之间的斜杠处理正确
        if (!barkUrl.endsWith("/")) {
            barkUrl += "/";
        }
        String url = barkUrl + barkDeviceKey + "/" + encodedTitle + "/" + encodedMessage;

        // Bark 这种路径参数形式直接使用 GET 请求即可，无需构建复杂的 RequestBody
        Request request = new Request.Builder()
                .url(url)
                .get() 
                .build();

        try (Response response = executeRequest(request)) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Bark响应异常: " + response.code());
            }
        }
    }
}
