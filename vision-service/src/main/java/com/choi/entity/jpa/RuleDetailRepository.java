package com.choi.entity.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.choi.entity.RuleDetail;
import com.choi.entity.RuleDetailPK;

public interface RuleDetailRepository extends JpaRepository<RuleDetail, RuleDetailPK> {
//	@Query(value = "select   m.*\r\n"
//			+ "from     booking_request m\r\n"
//			+ "where    request_dtm >= :baseDt\r\n"
//			+ "and      club_name = :clubName\r\n"
//			+ "order by request_dtm DESC", nativeQuery = true)
//	public List<BookingRequest> getRequestAllListWithClub(@Param("baseDt") String baseDt, @Param("clubName") String clubName);
//	
//	/**
//	 * @param requestId
//	 * @return
//	 */
//	public BookingRequest getBookingByRequestId(String requestId);
	
	@Query(value = "select   m.*\r\n"
			+ "from     rule_detail m\r\n"
			+ "where    uuid = :uuid\r\n", nativeQuery = true)			
	public List<RuleDetail> getRule(@Param("uuid") String uuid);
}
