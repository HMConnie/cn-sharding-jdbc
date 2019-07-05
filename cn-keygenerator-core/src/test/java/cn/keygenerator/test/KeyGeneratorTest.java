package cn.keygenerator.test;

import cn.keygenerator.core.dao.UserMapper;
import cn.keygenerator.core.entity.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class KeyGeneratorTest extends AbstractDAOTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RestTemplate restTemplate;


    @Test
    public void testInsert() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(URI.create("http://localhost:9091/keyGenerator/id"), String.class);
        String keyGeneratorId = responseEntity.getBody();
        User user = new User();
        user.setUserId(keyGeneratorId);
        user.setNickName(keyGeneratorId);

        userMapper.insert(user);
    }
}
