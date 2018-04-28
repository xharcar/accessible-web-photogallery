package cz.muni.fi.accessiblewebphotogallery.application.service;

import cz.muni.fi.accessiblewebphotogallery.application.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.AlbumService;
import cz.muni.fi.accessiblewebphotogallery.persistence.dao.AlbumDao;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {ApplicationConfig.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AlbumServiceTest {


    private AlbumService albumService;
    @Mock
    private AlbumDao albumDaoMock;

    private UserEntity defaultUser;


    @BeforeAll
    public void init(){}

    @Test
    public void findAllTest(){}

    @Test
    public void findByOwnerTest(){}

    @Test
    public void createAlbumTest(){}

    @Test
    public void saveAlbumTest(){}

    @Test
    public void addPhotoTest(){}

    @Test
    public void removePhotoTest(){}

    @Test
    public void listPhotosTest(){}

    @Test
    public void findByBase64IdTest(){}

}
