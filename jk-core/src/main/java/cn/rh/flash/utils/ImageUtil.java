package cn.rh.flash.utils;

import cn.rh.flash.bean.constant.Const;

public class ImageUtil {

    public static String getImage(String fileId){
        if (StringUtil.isNotEmpty(fileId)) {
            return Const.IMG_ADDR + fileId;
        }
        return "";
    }

}
