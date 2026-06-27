package com.leo.springai.quickstart;

import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionOptions;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisOptions;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisResponse;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesis;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisParam;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.junit.jupiter.api.Test;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.MimeTypeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;

/**
 * @author leoyin
 * @version JDK 8
 * @date 2026/6/27
 * @description
 */
@SpringBootTest
public class TestAliOmniModal {
    /**
     * 文生图
     */
    @Test
    public void testImageModel(@Autowired DashScopeImageModel imageModel) {
        DashScopeImageOptions imageOptions = DashScopeImageOptions.builder()
                .withModel("wanx-v1")
                .withWatermark(false)
                .build();

        ImageResponse imageResponse = imageModel.call(new ImagePrompt("粉红小猪大飞，香香软软", imageOptions));

        // 生成图片地址 url
        String url = imageResponse.getResult().getOutput().getUrl();
        System.out.println(url);

        // 生成base64
        // String b64Json = imageResponse.getResult().getOutput().getB64Json();
        // System.out.println(b64Json);

        // 文件流
        // InputStream in = new URL(url).openStream();
        // response.setHeader("Content-Type", MediaType.IMAGE_PNG_VALUE);
        // response.getOutputStream().write(in.readAllBytes());
        // response.getOutputStream().flush();
    }

    /**
     * 文生语音
     */
    @Test
    public void text2Voice(@Autowired DashScopeSpeechSynthesisModel speechSynthesisModel) throws IOException {
        DashScopeSpeechSynthesisOptions speechSynthesisOptions = DashScopeSpeechSynthesisOptions.builder()
                .voice("longanhuan")
                .speed(1.2f)
                .model("sambert-zhiyue-v1")
                .build();

        SpeechSynthesisResponse response = speechSynthesisModel.call(new SpeechSynthesisPrompt("大家好，我是你们的好朋友，大飞", speechSynthesisOptions));

        File file = new File(System.getProperty("user.dir") + "/语音合成.mp3");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            ByteBuffer byteBuffer = response.getResult().getOutput().getAudio();
            fos.write(byteBuffer.array());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * 语音文件翻译成文本（暂时阿里那边有问题，返回的字段 sentenceId 不匹配）
     */
    public static final String AUDIO_RESOURCES_URL = "https://dashscope.oss-cn-beijing.aliyuncs.com/samples/audio/paraformer/hello_world_male2.wav";
    @Test
    public void voice2Text(@Autowired DashScopeAudioTranscriptionModel audioTranscriptionModel) throws MalformedURLException {
        // String currentDir = System.getProperty("user.dir");
        // Path filePath = Paths.get(currentDir, "语音合成.mp3");

        DashScopeAudioTranscriptionOptions audioTranscriptionOptions = DashScopeAudioTranscriptionOptions.builder()
                .withModel("paraformer-v2")
                .build();

        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(new UrlResource(AUDIO_RESOURCES_URL), audioTranscriptionOptions);
        // AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(new FileSystemResource(filePath), audioTranscriptionOptions);

        AudioTranscriptionResponse response = audioTranscriptionModel.call(prompt);

        System.out.println(response.getResult().getOutput());
    }

    /**
     * 多模态：传给大模型图片、语音、视频，让其去理解（语音文件暂时不行，源码中没有音频的判断支持）
     */
    @Test
    public void multiModal(@Autowired DashScopeChatModel chatModel) {
        var audioFile = new ClassPathResource("/files/pic.png");
        Media media = new Media(MimeTypeUtils.IMAGE_PNG, audioFile);
        DashScopeChatOptions chatOptions = DashScopeChatOptions.builder()
                .withMultiModel(true) // 开启多模态
                .withModel("qwen3-vl-plus") // qwen3-vl-plus 视觉理解模型
                .build();
        Prompt prompt = Prompt.builder()
                .chatOptions(chatOptions)
                .messages(UserMessage.builder()
                        .media(media)
                        .text("识别图片")
                        .build())
                .build();
        ChatResponse response = chatModel.call(prompt);
        System.out.println(response.getResult().getOutput().getText());
    }

    /**
     * 文生视频：SpringAI 并没有提供对应的 API（https://docs.spring.io/spring-ai/reference/api/chatmodel.html）
     * 所以需要使用 alibaba 原生的 sdk 来支持
     */
    @Test
    public void text2Video() throws NoApiKeyException, InputRequiredException {
        VideoSynthesis vs = new VideoSynthesis();
        VideoSynthesisParam param = VideoSynthesisParam.builder()
                .model("wan2.7-t2v")
                .prompt("一只可爱的黑底白花的小猪在湖水中游泳")
                .size("1280*720")
                .apiKey(System.getenv("DASHSCOPE_ALI_API_KEY")) // 因为并没有用自动配置类，自然就不会使用 application.yml 中的 api-key
                .build();
        VideoSynthesisResult result = vs.call(param);
        System.out.println(result.getOutput().getVideoUrl());
    }
}
