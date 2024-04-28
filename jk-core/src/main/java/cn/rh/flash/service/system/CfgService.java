package cn.rh.flash.service.system;

import cn.rh.flash.bean.entity.system.Cfg;
import cn.rh.flash.bean.vo.system.CfgVo;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.dao.system.CfgRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.utils.EasyExcelUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * CfgService
 */

@Service
@Transactional
public class CfgService extends BaseService<Cfg, Long, CfgRepository> {
    @Autowired
    private ConfigCache configCache;

    public Cfg saveOrUpdate(Cfg cfg) {
        if (cfg.getId() == null) {
            insert(cfg);
        } else {
            update(cfg);
        }
        configCache.cache();
        return cfg;
    }

    @Override
    public void delete(Long id) {
        super.delete(id);
        configCache.cache();
    }

    public void exportV2(HttpServletResponse response, List<Cfg> records) {
        List<CfgVo> voList=new ArrayList<>();
         for (Cfg record : records) {
             CfgVo vo=new CfgVo();
             BeanUtils.copyProperties(record,vo);
             voList.add(vo);
         }
        EasyExcelUtil.export(response,"系统参数",voList, CfgVo.class);
    }


}
