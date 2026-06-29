package th.co.chayawat.commonapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.chayawat.commonapi.cms.jpa.entity.Users;
import th.co.chayawat.commonapi.cms.jpa.repository.UserRepository;
import th.co.chayawat.commonapi.dto.UsersDto;
import th.co.chayawat.commonapi.model.UsersRsp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private static Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    public List<UsersDto> getUserList(){
        System.out.println("Service : getUserList");
        List<Users> users = userRepository.findAll();
        List<UsersDto> usersDtos = new ArrayList<>();
        for(Users us:users){
            UsersDto UsersDto = new UsersDto();
            BeanUtils.copyProperties(us, UsersDto);
            usersDtos.add(UsersDto);
        }
        return usersDtos;
    }

    public UsersDto getUserById(Integer userId){
        System.out.println("Service : getUserById");
        Users users = userRepository.getReferenceById(userId);
        UsersDto usersDto = new UsersDto();
        BeanUtils.copyProperties(users, usersDto);
        return usersDto;
    }

    public UsersDto createUser(UsersRsp usersRsp){
        System.out.println("Service : createUser");
        Users user = new Users();
        user.setName(usersRsp.getName());
        user.setUsername(usersRsp.getUsername());
        user.setEmail(usersRsp.getEmail());
        user.setPhone(usersRsp.getPhone());
        user.setWebsite(usersRsp.getWebsite());
        Users users = userRepository.save(user);
        UsersDto usersDto = new UsersDto();
        BeanUtils.copyProperties(users, usersDto);
        return usersDto;
    }

    public UsersDto editUser(Integer userId, UsersRsp usersRsp){
        System.out.println("Service : editUser");
        Users user = userRepository.getReferenceById(userId);
        user.setName(usersRsp.getName());
        user.setUsername(usersRsp.getUsername());
        user.setEmail(usersRsp.getEmail());
        user.setPhone(usersRsp.getPhone());
        user.setWebsite(usersRsp.getWebsite());
        user.setModifiedDate(new Timestamp(System.currentTimeMillis()));
        Users users = userRepository.save(user);
        UsersDto usersDto = new UsersDto();
        BeanUtils.copyProperties(users, usersDto);
        return usersDto;
    }

    public Integer deleteUser(Integer userId){
        System.out.println("Service : deleteUser");
        userRepository.deleteById(userId);
        return userId;
    }


}
