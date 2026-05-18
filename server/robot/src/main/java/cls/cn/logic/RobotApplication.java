package cls.cn.logic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "cls.cn", exclude = {
        MongoAutoConfiguration.class,                 // 禁用 MongoDB
//        RedisAutoConfiguration.class                  // 禁用 Redis
})
@EnableScheduling
public class RobotApplication {

    static void main(String[] args) {
        SpringApplication.run(RobotApplication.class, args);
    }

}
