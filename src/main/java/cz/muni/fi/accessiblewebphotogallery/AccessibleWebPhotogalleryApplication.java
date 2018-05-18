package cz.muni.fi.accessiblewebphotogallery;

import cz.muni.fi.accessiblewebphotogallery.facade.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.facade.facade.UserFacade;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.util.Pair;

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

                /*UserEntity u1 = new UserEntity();
                u1.setAccountState(AccountState.ADMINISTRATOR);
                u1.setBio("Administrator of this site");
                u1.setEmail("422714@mail.muni.cz");
                u1.setLoginName("root");
                u1.setScreenName("Administrator");
                u1.setPasswordHash(new byte[]{0x53,0x42});
                u1.setPasswordSalt(new byte[]{(byte) 0xFC,0x19});
                u1 = ctx.getBean(UserDao.class).save(u1);
                System.out.println(u1);*/
                UserDto dto = new UserDto();
                UserFacade ufacade = ctx.getBean(UserFacade.class);
                dto.setAccountState(AccountState.ADMINISTRATOR);
                dto.setBio("Admin");
                dto.setEmail("422714@mail.muni.cz");
                dto.setLoginName("root");
                dto.setScreenName("Administrator");
                Pair<UserDto,String> reg = ufacade.registerUser(dto,"rootpassword635");
                if(reg == null) throw new RuntimeException("Failed to register administrator");
                System.out.println(reg);
                ufacade.confirmUserRegistration("422714@mail.muni.cz",reg.getSecond());
            }
        };
    }
}
