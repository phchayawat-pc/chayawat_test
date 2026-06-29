package controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import th.co.chayawat.commonapi.dto.UsersDto;
import th.co.chayawat.commonapi.controller.UsersController;
import th.co.chayawat.commonapi.service.UsersService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
public class UsersControllerTest {
    private MockMvc mockMvc;

    @Mock
    UsersService usersService; // จำลอง Service

    @InjectMocks
    private UsersController usersController; // คลาส Controller ที่เราต้องการทดสอบ (ปรับชื่อคลาสให้ตรงนะครับ)

    @BeforeEach
    void setUp() {
        // เซ็ตอัป MockMvc เพื่อใช้จำลองการยิง HTTP Request เข้ามาที่ Controller ตัวนี้
        mockMvc = MockMvcBuilders.standaloneSetup(usersController).build();
    }

    // เคสที่ 1: ดึงข้อมูลสำเร็จ (ต้องได้ HTTP Status 200 OK และได้ข้อมูลครบ)
    @Test
    void getUsers_Success_ShouldReturnOk() throws Exception {
        // 1. เตรียมข้อมูลสมมติ (Mock Data)
        List<UsersDto> mockList = new ArrayList<>();
        UsersDto user1 = new UsersDto();
        user1.setName("A");
        user1.setUsername("A");
        user1.setPhone("11111");
        user1.setEmail("A");
        user1.setWebsite("A");
        mockList.add(user1);

        // 2. กำหนดพฤติกรรมว่าถ้าเรียกเตะไปที่ Service ตัวนี้ ให้คืนค่า mockList กลับมานะ
        when(usersService.getUserList()).thenReturn(mockList);

        // 3. สั่งจำลองการยิง GET /users และตรวจสอบผลลัพธ์
        mockMvc.perform(get("/v1/users")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

}
