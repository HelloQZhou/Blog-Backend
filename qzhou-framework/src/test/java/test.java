import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qzhou.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class test {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void test1() {
        System.out.println("!");
    }
    @Test
    public void test2(){
        LambdaQueryWrapper<entity.User> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(entity.User::getUserName,"qzhou");
        entity.User user = userMapper.selectOne(queryWrapper);
        System.out.println(user!=null?1:0);
    }
}
