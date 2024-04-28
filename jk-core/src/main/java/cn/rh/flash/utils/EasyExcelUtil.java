package cn.rh.flash.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

public class EasyExcelUtil {


    /**
     * 导出
     * @param response
     * @param name 文件名称
     * @param data 数据源
     * @param object class对象
     */
    public static void export(HttpServletResponse response, String name, List data,Class<?> object){
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName =name+ cn.hutool.core.date.DateUtil.format(new Date(),"yyyyMMddHHmmss");
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream())
                    .head(object)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet(name)
                    .doWrite(data);
        } catch (IOException e) {
            throw new RuntimeException("导出失败");
        }
    }
}
