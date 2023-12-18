package com.choi.entity.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choi.entity.RuleMaster;

public interface RuleMasterRepository extends JpaRepository<RuleMaster, String> {
	public RuleMaster findByUuid(String uuid);
}
