package cn.keygenerator.controller;

import cn.keygenerator.generator.KeyGenerator;
import cn.keygenerator.utils.DbAndTableEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("keyGenerator")
public class KeyGeneratorController {

    @Autowired
    private KeyGenerator keyGenerator;

    @RequestMapping(value = "/id", method = RequestMethod.GET)
    public String getKeyGeneratorId() {
        String uuid = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        return keyGenerator.generateKey(DbAndTableEnum.USER, uuid);
    }
}
