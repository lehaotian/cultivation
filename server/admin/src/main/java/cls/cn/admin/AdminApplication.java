package cls.cn.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication(scanBasePackages = "cls.cn")
@EnableMongoRepositories(basePackages = "cls.cn.base.repository")
@EntityScan(basePackages = "cls.cn.base.entity")
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

}
