package cn.keygenerator.test;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan(basePackages = {"cn.keygenerator.core"})
@ImportResource({"classpath:applicationContext.xml","classpath:sharding-jdbc.xml","classpath:restTemplate.xml"})
public class ApplicationConfigTest {

}


