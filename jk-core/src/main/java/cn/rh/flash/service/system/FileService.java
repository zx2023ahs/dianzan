package cn.rh.flash.service.system;

import cn.rh.flash.bean.constant.cache.Cache;
import cn.rh.flash.bean.constant.cache.CacheKey;
import cn.rh.flash.bean.entity.system.FileInfo;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.dao.system.FileInfoRepository;
import cn.rh.flash.security.JwtUtil;
import cn.rh.flash.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
public class FileService extends BaseService<FileInfo, Long, FileInfoRepository> {
    @Autowired
    private ConfigCache configCache;
    @Autowired
    private FileInfoRepository fileInfoRepository;


    /**
     * 文件上传
    * @param multipartFile
     * @return
     */
    public FileInfo upload(MultipartFile multipartFile) {
        String uuid = UUID.randomUUID().toString();
        String realFileName = uuid + "." + multipartFile.getOriginalFilename().split("\\.")[1];
        try {

            File file = new File(configCache.get(ConfigKeyEnum.SYSTEM_FILE_UPLOAD_PATH).trim() + File.separator + realFileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            multipartFile.transferTo(file);
            return save(multipartFile.getOriginalFilename(), file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 创建文件
    * @param originalFileName
     * @param file
     * @return
     */
    public FileInfo save(String originalFileName, File file) {
        try {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setCreateBy(JwtUtil.getUserId());
            fileInfo.setOriginalFileName(originalFileName);
            fileInfo.setRealFileName(file.getName());
            insert(fileInfo);
            return fileInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Cacheable(value = Cache.APPLICATION, key = "'" + CacheKey.FILE_INFO + "'+#id")
    public FileInfo get(Long id) {
        FileInfo fileInfo = fileInfoRepository.getOne(id);
        fileInfo.setAblatePath(configCache.get(ConfigKeyEnum.SYSTEM_FILE_UPLOAD_PATH).trim() + File.separator + fileInfo.getRealFileName());
        return fileInfo;
    }

    public FileInfo uploadApi(MultipartFile multipartFile,long userId) {
        String uuid = "api_"+UUID.randomUUID().toString();
        String realFileName = uuid + "." + multipartFile.getOriginalFilename().split("\\.")[1];
        try {

            File file = new File(configCache.get(ConfigKeyEnum.SYSTEM_FILE_UPLOAD_PATH).trim() + File.separator + realFileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            multipartFile.transferTo(file);
            return saveApi(multipartFile.getOriginalFilename(), file,userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public FileInfo saveApi(String originalFileName, File file,long userId) {
        try {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setCreateBy( userId );
            fileInfo.setOriginalFileName(originalFileName);
            fileInfo.setRealFileName(file.getName());
            insert(fileInfo);
            return fileInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
