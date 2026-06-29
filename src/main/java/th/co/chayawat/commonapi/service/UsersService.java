package th.co.chayawat.commonapi.service;

import org.reactivestreams.Publisher;
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
import java.util.Optional;

@Service
public class UsersService {
    private static Logger log = LoggerFactory.getLogger(UsersService.class);

    @Autowired
    UserRepository userRepository;

    public List<UsersDto> getUserList(){
        log.info("Service : getUserList");
        try{
            List<Users> users = userRepository.findAll();
            List<UsersDto> usersDtos = new ArrayList<>();
            if(users.isEmpty()){
                throw new IllegalArgumentException("The information was not found.");
            }else{
                for(Users us:users){
                    UsersDto UsersDto = new UsersDto();
                    BeanUtils.copyProperties(us, UsersDto);
                    usersDtos.add(UsersDto);
                }
                return usersDtos;
            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    public UsersDto getUserById(Integer userId){
        log.info("Service : getUserById");
        try {
            Optional<Users> usersOptional = userRepository.findById(userId);
            if(usersOptional.isEmpty()){
                throw new IllegalArgumentException("The information was not found or this ID does not exist.");
            }else{
                Users users = usersOptional.get();
                UsersDto usersDto = new UsersDto();
                BeanUtils.copyProperties(users, usersDto);
                return usersDto;
            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    public UsersDto createUser(UsersRsp usersRsp){
        log.info("Service : createUser");
        try{
            if (usersRsp == null) {
                throw new IllegalArgumentException("Request body cannot be null");
            }
            if (usersRsp.getName() == null || usersRsp.getName().trim().isEmpty() ||
                    usersRsp.getUsername() == null || usersRsp.getUsername().trim().isEmpty() ||
                    usersRsp.getEmail() == null || usersRsp.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("All fields (name, username, email) are required and cannot be null or empty");
            }
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
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }

    }

    public UsersDto editUser(Integer userId, UsersRsp usersRsp){
        log.info("Service : editUser");
        try{
            if (usersRsp == null) {
                throw new IllegalArgumentException("Request body cannot be null");
            }
            if (usersRsp.getName() == null || usersRsp.getName().trim().isEmpty() ||
                    usersRsp.getUsername() == null || usersRsp.getUsername().trim().isEmpty() ||
                    usersRsp.getEmail() == null || usersRsp.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("All fields (name, username, email) are required and cannot be null or empty");
            }
            Optional<Users> usersOptional = userRepository.findById(userId);
            if(usersOptional.isEmpty()){
                throw new IllegalArgumentException("The information was not found or this ID does not exist.");
            }
            Users user = usersOptional.get();
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
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    public Integer deleteUser(Integer userId){
        log.info("Service : deleteUser");
        try{
            Optional<Users> usersOptional = userRepository.findById(userId);
            if(usersOptional.isEmpty()){
                throw new IllegalArgumentException("The information was not found or this ID does not exist.");
            }
            userRepository.deleteById(userId);
            return userId;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }

    }
}
