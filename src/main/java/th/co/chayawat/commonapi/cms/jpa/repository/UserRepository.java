package th.co.chayawat.commonapi.cms.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th.co.chayawat.commonapi.cms.jpa.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
}
