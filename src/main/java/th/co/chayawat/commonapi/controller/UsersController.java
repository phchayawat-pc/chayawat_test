package th.co.chayawat.commonapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping(value = "/v1")
public class UsersController {

    private static Logger log = LoggerFactory.getLogger(UsersController.class);



}
