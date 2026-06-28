package th.co.truecorp.commonapi.reward.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.model.UsersRsp;

import java.util.List;

@Service
public class UserService {
    private static Logger log = LoggerFactory.getLogger(UserService.class);

    public List<UsersRsp> getUserList(){
        System.out.println("Service : getUserList");
        return null;
    }
}
