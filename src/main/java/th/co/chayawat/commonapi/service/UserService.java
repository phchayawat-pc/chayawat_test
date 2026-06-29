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

}
