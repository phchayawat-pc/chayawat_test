package th.co.truecorp.commonapi.reward.controller;

import com.google.gson.Gson;
import io.lettuce.core.TransactionResult;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import th.co.truecorp.commonapi.reward.model.UsersRsp;
import th.co.truecorp.commonapi.reward.service.UserService;
import th.co.truecorp.commonlib.ws.model.GenericJsonResponse;

import java.util.HashMap;
import java.util.List;

@Validated
@RestController
@RequestMapping(value = "/v1")
public class UsersController {

    private static Logger log = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    UserService userService;

    String host;

    Gson gson = new Gson();


    @GetMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UsersRsp> getUsers(

    ) throws Exception {
        System.out.println("getUsers");
        List<UsersRsp> usersRspList = userService.getUserList();
        return usersRspList;
    }

}
