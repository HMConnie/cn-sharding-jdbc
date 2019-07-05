package cn.keygenerator.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@ComponentScan(basePackages = {"cn.keygenerator.core"})
@ImportResource({"classpath:applicationContext.xml", "classpath:sharding-jdbc.xml", "classpath:restTemplate.xml"})
public class Main {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
        System.out.println("audit task core service start");
    }
}
