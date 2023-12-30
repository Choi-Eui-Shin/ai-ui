package com.choi.entity.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.choi.entity.RuleMaster;

public interface RuleMasterRepository extends JpaRepository<RuleMaster, String> {
	
	@Query(value = "select   m.*\r\n"
			+ "from     rule_master m\r\n"
			+ "where    user_id IN (:owner, 'SYSTEM')", nativeQuery = true)	
	public List<RuleMaster> getTemplageList(@Param("owner") String owner);
}
