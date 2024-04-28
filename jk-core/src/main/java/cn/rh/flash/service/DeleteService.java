package cn.rh.flash.service;


import org.springframework.transaction.annotation.Transactional;

public interface DeleteService<ID> {

    /**
     * 根据主键删除记录
    * @param id 主键
     */
    @Transactional
    void delete(ID id);

    /**
     * 根据主键删除记录
    * @param ids 主键集合
     */
    @Transactional
    void delete(Iterable<ID> ids);

    /**
     * 清空表数据
     */
    @Transactional
    void clear();

    /**
     * 使用truncate table 清空数据
    */
    @Transactional
    void truncate();
}
