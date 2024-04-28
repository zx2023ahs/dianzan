package cn.rh.flash.dao.system;


import cn.rh.flash.bean.entity.system.Role;
import cn.rh.flash.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface RoleRepository extends BaseRepository<Role, Long> {
    @Query(nativeQuery = true, value = "SELECT id AS ID, pid AS pId, name AS NAME, ( CASE WHEN (pid = 0 OR pid IS NULL) THEN 'true' ELSE 'false' END ) OPEN FROM t_sys_role")
    List roleTreeList();

    @Query(nativeQuery = true, value = "SELECT r.id AS ID, pid AS pId, name AS NAME, ( CASE WHEN (pid = 0 OR pid IS NULL) THEN 'true' ELSE 'false' END ) \"open\", ( CASE WHEN (r1.id = 0 OR r1.id IS NULL) THEN 'false' ELSE 'true' END ) AS checked FROM t_sys_role r LEFT JOIN ( SELECT id FROM t_sys_role WHERE id IN (?1)) r1 ON r.id = r1.id ORDER BY pid, num ASC")
    List roleTreeListByRoleId(Long[] ids);

    List findByName(String roleName);
}
