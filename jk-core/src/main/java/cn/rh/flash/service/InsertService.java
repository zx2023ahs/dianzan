package cn.rh.flash.service;


import org.springframework.transaction.annotation.Transactional;

public interface InsertService<T, ID> {

    /**
     * 添加一条数据
    * @param record 要添加的数据
     * @return 添加后生成的主键
     */
    @Transactional
    T insert(T record);
}
