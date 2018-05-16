package cz.muni.fi.accessiblewebphotogallery;

import cz.muni.fi.accessiblewebphotogallery.persistence.dao.UserDao;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class AccessibleWebPhotogalleryApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccessibleWebPhotogalleryApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                String[] beans = ctx.getBeanDefinitionNames();
                Arrays.sort(beans);
                for (String bean : beans) {
                    System.out.println(bean);
                }

                UserEntity u1 = new UserEntity();
                u1.setAccountState(AccountState.ADMINISTRATOR);
                u1.setBio("Administrator of this site");
                u1.setEmail("422714@mail.muni.cz");
                u1.setLoginName("root");
                u1.setScreenName("Administrator");
                u1.setPasswordHash(new byte[]{0x53,0x42});
                u1.setPasswordSalt(new byte[]{(byte) 0xFC,0x19});
                u1 = ctx.getBean(UserDao.class).save(u1);
                System.out.println(u1);
            }
        };
    }
}
