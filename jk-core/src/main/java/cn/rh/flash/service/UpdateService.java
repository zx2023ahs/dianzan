package cn.rh.flash.service;


import org.springframework.transaction.annotation.Transactional;

public interface UpdateService<T, ID> {
    /**
     * 修改记录信息
    * @param record 要修改的对象
     * @return 返回修改的记录
     */
    @Transactional
    T update(T record);
}
