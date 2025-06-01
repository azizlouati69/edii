package repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import entities.model.client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface clientrepository extends JpaRepository<client, Long> {

   @Query("SELECT COUNT(c) FROM client c")
   Long countAllClients();

   // Get count of active clients (those with at least one order)
   @Query("SELECT COUNT(c) FROM client c WHERE SIZE(c.orders) > 10")
   Long countActiveClients();

   // Get count of inactive clients (those with no orders)
   @Query("SELECT COUNT(c) FROM client c WHERE SIZE(c.orders) < 10")
   Long countInactiveClients();

   void deleteByBuyerIdentifier(String buyerIdentifier);

   // Calculate the average number of orders per client
   @Query("SELECT AVG(SIZE(c.orders)) FROM client c")
   Double averageOrdersPerClient();

   // Get the top clients based on order count
   @Query("SELECT c.id, c.buyerIdentifier, c.senderId, SIZE(c.orders) FROM client c ORDER BY SIZE(c.orders) DESC")
   List<Object[]> findTopClientsByOrderCount(Pageable pageable);

   @Query("""
        SELECT c.id, c.buyerIdentifier, c.senderId, MAX(o.issueDate)
        FROM client c
        JOIN c.orders o
        GROUP BY c.id, c.buyerIdentifier, c.senderId
        ORDER BY MAX(o.issueDate) DESC
    """)
   List<Object[]> findTopClientsByRecentOrders(Pageable pageable);

   List<client> findBySenderId(String senderId);
   List<client> findByBuyerIdentifier(String buyerIdentifier);
   Optional<client> findById(Long Id);
   List<client> findBySenderIdContainingIgnoreCase(String senderId);
   List<client> findByBuyerIdentifierContainingIgnoreCase(String buyerIdentifier);

   @Query("""
        SELECT c.id, c.buyerIdentifier, c.senderId, COUNT(o.id) AS orderCount
        FROM client c
        JOIN c.orders o
        WHERE o.issueDate BETWEEN :startDate AND :endDate
        GROUP BY c.id, c.buyerIdentifier, c.senderId
        ORDER BY orderCount DESC
    """)
   List<Object[]> findTopClientsByOrderCountThisMonth(@Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);

   @Query("""
        SELECT c.id, c.buyerIdentifier, c.senderId, COUNT(o.id) AS orderCount
        FROM client c
        JOIN c.orders o
        WHERE o.issueDate BETWEEN :startOfYear AND :endOfYear
        GROUP BY c.id, c.buyerIdentifier, c.senderId
        ORDER BY orderCount DESC
    """)
   List<Object[]> findTopClientsByOrderCountThisYear(@Param("startOfYear") LocalDate startOfYear,
                                                     @Param("endOfYear") LocalDate endOfYear);

   @Query("""
        SELECT c.id, c.buyerIdentifier, c.senderId, COUNT(o.id) AS orderCount
        FROM client c
        JOIN c.orders o
        WHERE o.issueDate BETWEEN :startOfWeek AND :endOfWeek
        GROUP BY c.id, c.buyerIdentifier, c.senderId
        ORDER BY orderCount DESC
    """)
   List<Object[]> findTopClientsByOrderCountThisWeek(@Param("startOfWeek") LocalDate startOfWeek,
                                                     @Param("endOfWeek") LocalDate endOfWeek);

   @Query("""
        SELECT c.id, c.buyerIdentifier, c.senderId, COUNT(o.id) AS orderCount
        FROM client c
        JOIN c.orders o
        WHERE o.issueDate = :today
        GROUP BY c.id, c.buyerIdentifier, c.senderId
        ORDER BY orderCount DESC
    """)
   List<Object[]> findTop3ClientsByOrderCountToday(@Param("today") LocalDate today, Pageable pageable);

   @Query("""
        SELECT COUNT(c)
        FROM client c
        WHERE (
            SELECT COUNT(o)
            FROM c.orders o
            WHERE o.issueDate BETWEEN :startDate AND :endDate
        ) > 10
    """)
   Long countActiveClientsThisMonth(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

   @Query("""
        SELECT COUNT(c)
        FROM client c
        WHERE (
            SELECT COUNT(o)
            FROM c.orders o
            WHERE o.issueDate BETWEEN :startDate AND :endDate
        ) < 10
    """)
   Long countInactiveClientsThisMonth(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);
}
