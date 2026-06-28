package th.co.chayawat.commonapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.co.chayawat.commonapi.cms.jpa.entity.Users;
import th.co.chayawat.commonapi.cms.jpa.repository.UserRepository;
import th.co.chayawat.commonapi.dto.UsersDto;
import th.co.chayawat.commonapi.model.UsersRsp;

import java.util.List;

@Service
public class UserService {
    private static Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    public List<UsersDto> getUserList(){
        System.out.println("Service : getUserList");
        List<Users> users = userRepository.findAll();
        return null;
    }

    public UsersDto getUserById(String userId){
        System.out.println("Service : getUserById");
        Users users = userRepository.getReferenceById(Integer.parseInt(userId));
        return null;
    }

    public UsersDto createUser(UsersRsp usersRsp){
        System.out.println("Service : getUserById");
        Users user = new Users();
        Users users = userRepository.save(user);
        return null;
    }

    public UsersDto editUser(String userId, UsersRsp usersRsp){
        System.out.println("Service : getUserById");
        Users user = new Users();
        Users users = userRepository.save(user);
        return null;
    }

    public String deleteUser(String userId){
        System.out.println("Service : getUserById");
        userRepository.deleteById(Integer.parseInt(userId));
        return userId;
    }


}
