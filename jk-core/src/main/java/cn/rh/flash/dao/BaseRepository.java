package cn.rh.flash.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 封装基础的dao接口
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>
        , PagingAndSortingRepository<T, ID>
        , JpaSpecificationExecutor<T> {
    List<Map> queryMapBySql(String sql);

    /**
     * 根据原生sql查询数组对象
    * @param sql
     * @return
     */
    Map getMapBySql(String sql);

    /**
     * 根据原生sql查询对象列表
    * @param sql
     * @return
     */
    List<T> queryBySql(String sql);

    List<?> queryObjBySql(String sql, Class<?> klass);


    /**
     * 根据原生sql查询对象列表
    * @param sql
     * @return
     */
    List<T> query(String sql);


    /**
     * 根据原生sql查询对象
    * @param sql
     * @return
     */
    T get(String sql);

    T getNull(String sql);

    T getOne(ID id);

    /**
     * 执行sql
    * @param sql
     * @return
     */
    @Transactional
    int execute(String sql);

    /**
     * 获取数据类型
    * @return
     */
    Class<T> getDataClass();

    /**
     * 使用truncate table 清空数据
     */
    @Transactional
    int truncate();

    /**
     * 重写事务保存
     */
    @Transactional
    @Override
    <S extends T> S save(S s);

    /**
     * 重写删除事务
     */
    @Transactional
    @Override
    void deleteById(ID id);

    /**
     * 重写删除事务
     */
    @Transactional
    @Override
    void delete(T t);

    /**
     * 批量添加
     */
    @Transactional
    @Override
    <S extends T> List<S> saveAll(Iterable<S> iterable);

    @Transactional
    @Override
    void deleteAll();

    @Transactional
    @Override
    void deleteAll(Iterable<? extends T> iterable);

}
