package com.choi.entity.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.choi.entity.ConvertHistory;
import com.choi.entity.ConvertHistoryPK;
import com.choi.entity.Users;

public interface ConvertRepository extends JpaRepository<ConvertHistory, ConvertHistoryPK> {
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
}
