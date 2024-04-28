package cn.rh.flash.service.dzvip;


import cn.rh.flash.bean.dto.api.VipMessageDetailDto;
import cn.rh.flash.bean.entity.dzsys.MultilingualLang;
import cn.rh.flash.bean.entity.dzvip.DzVipMessage;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.vo.api.VipMessageDetailVo;
import cn.rh.flash.bean.vo.api.VipMessageVo;
import cn.rh.flash.bean.vo.dz.DzVipCountVo;
import cn.rh.flash.bean.vo.query.DynamicSpecifications;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.dao.dzvip.DzVipMessageRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.dzsys.MultilingualLangService;
import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DzVipMessageService extends BaseService<DzVipMessage, Long, DzVipMessageRepository> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private DzVipMessageRepository dzVipMessageRepository;

    @Autowired
    private ApiUserCoom apiUserCoom;

    @Autowired
    private ConfigCache configCache;

    @Autowired
    private MultilingualLangService multilingualLangService;

    public List<VipMessageVo> findAllVo() {
        Specification<DzVipMessage> powerBankSpecification = DynamicSpecifications.bySearchFilter(SearchFilter.build("dzstatus", SearchFilter.Operator.NE, "3"), DzVipMessage.class);
        return dzVipMessageRepository.findAll(powerBankSpecification).stream().map(v -> {
            VipMessageVo vipMessageVo = new VipMessageVo();
            BeanUtils.copyProperties(v, vipMessageVo);
            String color = ConstantFactory.me().getDictsByName("vip按钮颜色", v.getVipType());

            if (StringUtils.isNotEmpty(color)) {
                String[] s = color.split("_");
                if (2 == s.length) {
                    vipMessageVo.setButtonColor(s[0]);
                    vipMessageVo.setColor(s[1]);
                }
            }
            if (vipMessageVo.getVipType().equals(apiUserCoom.getVipType())) {
                vipMessageVo.setFlg(1);
            } else {
                vipMessageVo.setFlg(2);
            }
//            // 是否续费
//            boolean expired = DateUtil.isExpired(apiUserCoom.getVipExpireDate());
//            if (apiUserCoom.getVipType().equals(vipMessageVo.getVipType())  && expired){  // 过期 本级vip可续费
//                vipMessageVo.setFlg(3);
//            }

            return vipMessageVo;
        }).collect(Collectors.toList());
    }

    public VipMessageDetailVo getVipMessageDetail(VipMessageDetailDto dto) {
        DzVipMessage one = get(SearchFilter.build("idw", dto.getIdw()));
        VipMessageDetailVo vipMessageDetailVo = new VipMessageDetailVo();
        BeanUtils.copyProperties(one, vipMessageDetailVo);
        String color = ConstantFactory.me().getDictsByName("vip按钮颜色", vipMessageDetailVo.getVipType());
        if (StringUtils.isNotEmpty(color)) {
            String[] s = color.split("_");
            if (2 == s.length) {
                vipMessageDetailVo.setButtonColor(s[0]);
                vipMessageDetailVo.setColor(s[1]);
            }
        }
        if (StringUtil.isNotEmpty(one.getLangKey())) {
            // 查询多语言
            if (StringUtil.isEmpty(dto.getLangCode())) {
                dto.setLangCode("ZH_EN"); // 没有 就默认英语
            }
            List<SearchFilter> filters = new ArrayList<>();
            filters.add(SearchFilter.build("langKey", one.getLangKey()));
            filters.add(SearchFilter.build("langCode", dto.getLangCode()));
            MultilingualLang multilingualLang = multilingualLangService.get(filters);

            if (multilingualLang == null && !"ZH_EN".equals(dto.getLangCode())) {
                filters.clear();
                filters.add(SearchFilter.build("langKey", one.getLangKey()));
                filters.add(SearchFilter.build("langCode", "ZH_EN"));
                multilingualLang = multilingualLangService.get(filters);
            }
            vipMessageDetailVo.setLangContext(multilingualLang == null ? null : multilingualLang.getLangContext());
        }

//        // 是否续费
//        boolean expired = DateUtil.isExpired(apiUserCoom.getVipExpireDate());
//        if (apiUserCoom.getVipType().equals(vipMessageDetailVo.getVipType())  && expired){  // 过期 本级vip可续费
//            vipMessageDetailVo.setFlg(3);
//        }

        return vipMessageDetailVo;
    }

    public DzVipCountVo findVipCount(String ucode) {
        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        String sql = DzVipMessageServiceSql.findVipCount(ucode, testCode);

        Map mapBySql = dzVipMessageRepository.getMapBySql(sql);
        return BeanUtil.mapToBean(mapBySql, new DzVipCountVo());
    }


    public String findVipImg(Long uid) {
        // skj 1-28 根据当前用户Vip 返回不同的充电宝产品图片
        String sql = DzVipMessageServiceSql.findVipImgSql(uid);
        Map map = dzVipMessageRepository.getMapBySql(sql);
        String powerBankImg = (String) map.get("powerBankImg");
        return powerBankImg;
    }

    public List<DzVipMessage> getRandomRecord() {
        List<DzVipMessage> dzVipMessages = new ArrayList<>();
        List<Map> maps = dzVipMessageRepository.queryMapBySql(DzVipMessageServiceSql.getRandomRecord());
        for (Map map : maps) {
            DzVipMessage dzVipMessage = new DzVipMessage();
            dzVipMessage.setName((String) map.get("name"));
            dzVipMessages.add(dzVipMessage);
        }
        return dzVipMessages;
    }

}

