package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.voice;
import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.SynthesizerTool;
import com.baidu.tts.client.TtsMode;
import edu.zjut.androiddeveloper_ailaiziciqi.baiduvoice.control.InitConfig;
import edu.zjut.androiddeveloper_ailaiziciqi.baiduvoice.control.MySyntherizer;
import edu.zjut.androiddeveloper_ailaiziciqi.baiduvoice.control.NonBlockSyntherizer;
import edu.zjut.androiddeveloper_ailaiziciqi.baiduvoice.listener.UiMessageListener;
import edu.zjut.androiddeveloper_ailaiziciqi.baiduvoice.util.Auth;
import edu.zjut.androiddeveloper_ailaiziciqi.baiduvoice.util.AutoCheck;
import edu.zjut.androiddeveloper_ailaiziciqi.baiduvoice.util.FileUtil;
import edu.zjut.androiddeveloper_ailaiziciqi.baiduvoice.util.IOfflineResourceConst;
import edu.zjut.androiddeveloper_ailaiziciqi.baiduvoice.util.OfflineResource;
public class VoiceAssistant {
    // ================== 完整版初始化参数设置开始 ==========================
    /**
     * 发布时请替换成自己申请的appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
     * 本demo的包名是com.baidu.tts.sample，定义在build.gradle中。
     */
    protected String appId;

    protected String appKey;

    protected String secretKey;

    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； TtsMode.OFFLINE 纯离线合成，需要纯离线SDK
    protected TtsMode ttsMode = IOfflineResourceConst.DEFAULT_SDK_TTS_MODE;

    protected boolean isOnlineSDK = TtsMode.ONLINE.equals(IOfflineResourceConst.DEFAULT_SDK_TTS_MODE);
}
