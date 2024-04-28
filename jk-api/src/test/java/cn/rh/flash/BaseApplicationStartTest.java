package cn.rh.flash;

import cn.rh.flash.api.ApiApplication;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 测试模块基类<br>
 * 不要直接在该类中编写测试代码，而是通过继承该类。 参考DeptServiceTest,BaseRepositoryTest等
*/

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ApiApplication.class)
@TestPropertySource(locations = {"classpath:application.yml"})
public class BaseApplicationStartTest {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Ignore
    @Test
    public void makeTestPass() {

    }

}
