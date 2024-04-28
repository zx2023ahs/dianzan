package cn.rh.flash.bean.vo.query;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 将SearchFilter查询条件解析为jpa查询对象Predicate
 */
public class DynamicSpecifications {

    public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> filters, final Class<T> entityClazz) {
        SimpleSpecification<T> simpleSpecification = new SimpleSpecification<T>(filters);
        return simpleSpecification;
    }

    public static <T> Specification<T> bySearchFilter( SearchFilter filter, final Class<T> entityClazz) {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add( filter );
        SimpleSpecification<T> simpleSpecification = new SimpleSpecification<T>(filters);
        return simpleSpecification;
    }
}
