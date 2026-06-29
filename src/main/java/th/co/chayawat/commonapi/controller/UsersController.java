package th.co.chayawat.commonapi.controller;

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
import th.co.chayawat.commonapi.service.UsersService;

import java.util.List;

@Validated
@RestController
@RequestMapping(value = "/v1")
public class UsersController {
    private static Logger log = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    UsersService usersService;

    @GetMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UsersDto>> getUsers(
    ) throws Exception {
        log.info("getUsers");
        try {
            List<UsersDto> usersDtoList = usersService.getUserList();
            return new ResponseEntity<List<UsersDto>>(usersDtoList, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsersDto> getUserById(
            @PathVariable(value = "userId") Integer userId
    ) throws Exception {
        log.info("getUserById : "+userId);
        try {
            if(userId != null) {
                UsersDto usersDto = usersService.getUserById(userId);
                return new ResponseEntity<>(usersDto, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsersDto> createUser(
            @RequestBody UsersRsp body
    ) throws Exception {
        log.info("createUser");
        try {
            if(body != null){
                UsersDto dto = usersService.createUser(body);
                return new ResponseEntity<UsersDto>(dto, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsersDto> editUser(
            @PathVariable(value="userId") Integer userId,
            @RequestBody UsersRsp body
    ) throws Exception {
        log.info("editUser");
        try {
            if(userId != null && body != null){
                UsersDto dto = usersService.editUser(userId, body);
                return new ResponseEntity<UsersDto>(dto, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> deleteUser(
            @PathVariable(value="userId") Integer userId
    ) throws Exception {
        log.info("deleteUser");
        try {
            if(userId != null) {
                userId = usersService.deleteUser(userId);
                return new ResponseEntity<Integer>(userId, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
