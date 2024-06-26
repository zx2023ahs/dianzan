package cn.rh.flash.bean.constant.factory;

import cn.rh.flash.bean.constant.state.Order;
import cn.rh.flash.utils.HttpUtil;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;

import javax.servlet.http.HttpServletRequest;

/**
 *  Table默认的分页参数创建
 */
public class PageFactory<T> {

    public Page<T> defaultPage() {
        HttpServletRequest request = HttpUtil.getRequest();

        String limitStr = request.getParameter("limit");
        int limit =  10;
        if(StringUtil.isNotEmpty(limitStr)){
          limit = Integer.valueOf(limitStr);
        }
        String pageNum = request.getParameter("page");
        int current = 1;

        if (StringUtils.isNotEmpty(pageNum)) {
            current = Integer.valueOf(pageNum);
        }
        //排序字段名称
        String sortName = request.getParameter("sort");
        //asc或desc(升序或降序)
        String order = request.getParameter("order");
        Page<T> page = new Page<>(current, limit);
        if (StringUtil.isNotEmpty(sortName)) {
            Sort.Direction direction = Order.ASC.getDes().equals(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sort = Sort.by(direction, sortName);
            page.setSort(sort);
        }
        return page;
    }
}
