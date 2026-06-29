package th.co.chayawat.commonapi.cms.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import th.co.chayawat.commonapi.cms.jpa.entity.Users;

public interface UserRepository extends JpaRepository<Users, Integer> {

}
