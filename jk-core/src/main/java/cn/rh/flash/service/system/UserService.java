package cn.rh.flash.service.system;

import cn.rh.flash.bean.entity.system.User;
import cn.rh.flash.cache.CacheDao;
import cn.rh.flash.dao.system.UserRepository;
import cn.rh.flash.security.JwtUtil;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService extends BaseService<User, Long, UserRepository> {
    private Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CacheDao cacheDao;
    @Value("${jwt.token.expire.time}")
    private Integer tokenExpireTime;

    @Autowired
    private ApiUserCoom apiUserCoom;



    /**
     * 登录查询用户的时候不从缓存中查询
     * @param account
     * @return
     */
    public User findByAccountForLogin(String account) {
        User user = userRepository.findByAccount(account);
        cacheDao.hset(CacheDao.SESSION,account,user, tokenExpireTime );
        return user;
    }

    public User findByAccount(String account) {
        User user = cacheDao.hget(CacheDao.SESSION, account, User.class);
        if (user != null) {
            return user;
        }
        return findByAccountForLogin(account);
    }
    public User findByPhone(String phone) {
        User  user = userRepository.findByPhone(phone);
        cacheDao.hset(CacheDao.SESSION, phone, user);
        return user;
    }

    @Override
    public User update(User record) {
        User user = super.update(record);
        cacheDao.hset(CacheDao.SESSION, user.getAccount(), user);
        return user;
    }

    /**
     * 根据用户信息生成token
    * @param user
     * @return
     */
    public String loginForToken(User user) {
        //获取用户token值
        String token = JwtUtil.sign(user, tokenExpireTime * 60000);
        //将token作为RefreshToken Key 存到缓存中，缓存时间为token有效期的两倍
        String refreshTokenCacheKey = token;
        Date expireDate = new Date(System.currentTimeMillis() + tokenExpireTime * 120000);
        cacheDao.hset(CacheDao.SESSION, refreshTokenCacheKey, String.valueOf(expireDate.getTime()));
        return token;
    }

    /**
     * 获取refreshToken是否有效
    * @param token
     * @return
     */
    public boolean refreshTokenIsValid(String token) {
        String refreshTokenTime = (String) cacheDao.hget(CacheDao.SESSION, token);
        if (refreshTokenTime == null) {
            return false;
        }
        return System.currentTimeMillis() <= Long.valueOf(refreshTokenTime);
    }

    /**
     * 添加管理账号  并 同步创建 移动端 账号  通过  ucode 关联
     * @param user
     * @param password
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertAll(User user,String password, String registerIpCity) throws Exception {
        User save = userRepository.save(user);
        // 同步创建 移动端 账号  通过  ucode 关联
//        String account = save.getPhone();
//        String[] accountstr =  account.split("-" );

        apiUserCoom.createAMobileAccount ( 2,save.getName(),save.getPhone() ,
                save.getAccount() , password,"", save.getUcode(),1,null,registerIpCity,"",save.getName());
    }

    // 同步修改顶级账号 账号字段
    @Transactional(rollbackFor = Exception.class)
    public void updateAll(User oldUser) {
        apiUserCoom.updateMobileAccount(oldUser);
    }


    //根据条件查询Ucode集合
    public Set<String> getUcode(String testCode) {
        Set<String> strings=new HashSet<>();
        String sql ="SELECT ucode FROM t_sys_user WHERE deptid = 3 and ucode != '"+ testCode+"' GROUP BY ucode";
        List<User> uscode =(List<User>) userRepository.queryObjBySql(sql,User.class);
        for (User user : uscode) {
            strings.add(user.getUcode());
        }
        return strings;
    }

    /**
     * 查询user  uscode最新方法
     * @param testCode
     * @return
     */
    public Set<String> newGetUcode(String testCode) {
        Set<String> strings=new HashSet<>();
        String sql ="SELECT t_sys_user.* FROM t_sys_user WHERE deptid = 3 and ucode != '"+ testCode+"' GROUP BY ucode";
        List<User> uscode = userRepository.queryBySql(sql);
        for (User user : uscode) {
            strings.add(user.getUcode());
        }
        return strings;
    }


}
