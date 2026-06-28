package th.co.chayawat.commonapi.controller;

import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import th.co.chayawat.commonapi.dto.UsersDto;
import th.co.chayawat.commonapi.model.UsersRsp;
import th.co.chayawat.commonapi.service.UserService;
import java.util.List;

@Validated
@RestController
@RequestMapping(value = "/v1")
public class UsersController {

    private static Logger log = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    UserService userService;

    @GetMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UsersDto>> getUsers(

    ) throws Exception {
        System.out.println("getUsers");
        List<UsersDto> usersDtoList = userService.getUserList();
        return new ResponseEntity<List<UsersDto>>(usersDtoList, HttpStatus.OK);
    }

    @GetMapping(path = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsersDto> getUserById(
            @RequestParam(value="userId") String userId
    ) throws Exception {
        System.out.println("getUserById");
        UsersDto usersDto = null;
        return new ResponseEntity<UsersDto>(usersDto, HttpStatus.OK);
    }

    @PostMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsersDto> createUser(
            @RequestBody UsersRsp body
    ) throws Exception {
        System.out.println("createUser");
        return new ResponseEntity<UsersDto>(new UsersDto(), HttpStatus.OK);
    }

    @PutMapping(path = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsersDto> editUser(
            @RequestParam(value="userId") String userId,
            @RequestBody UsersRsp body
    ) throws Exception {
        System.out.println("editUser");
        return new ResponseEntity<UsersDto>(new UsersDto(), HttpStatus.OK);
    }

    @DeleteMapping(path = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteUser(
            @RequestParam(value="userId") String userId,
            @RequestBody UsersRsp body
    ) throws Exception {
        System.out.println("deleteUser");
        return new ResponseEntity<String>(userId, HttpStatus.OK);
    }

}
