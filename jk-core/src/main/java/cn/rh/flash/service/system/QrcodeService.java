package cn.rh.flash.service.system;

import cn.rh.flash.cache.CacheDao;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 二维码登录服务
 *
 * @Author enilu
 * @Date 2021/8/31 20:06
 * @Version 1.0
 */
@Service
public class QrcodeService {
    // 二维码状态：
    // 未扫描
    public static final String UNDO = "undo";
    // 扫描成功
    public static final String SUCCESS = "success";
    // 取消登录
    public static final String CANCEL = "cancel";
    // 二维码过期
    public static final String INVALID = "invalid";

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService userService;
    /**
     * 二维码有效期2分钟
     */
    @Autowired
    public CacheDao cacheDao;

    /**
     * 生成二维码
     *
     * @return
     */
    public BitMatrix createQrcode(String uuid) {
        logger.info("set:{}={}",uuid,UNDO);
        cacheDao.hset(CacheDao.SHORT, uuid, UNDO, 0);
        QRCodeWriter writer = new QRCodeWriter();
        // 其他参数，如字符集编码
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 容错级别为H
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 白边的宽度，可取0~4
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new MultiFormatWriter().encode(uuid, BarcodeFormat.QR_CODE, 200, 200, hints);
        } catch (WriterException e) {
            logger.error("createQrcode error", e);
        }
        return bitMatrix;
    }

}

