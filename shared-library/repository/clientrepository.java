
package  repository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import entities.model.client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
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
    SELECT c.id, c.buyerIdentifier, c.senderId, MAX(o.issueDate)\s
    FROM client c\s
    JOIN c.orders o\s
    GROUP BY c.id, c.buyerIdentifier, c.senderId\s
    ORDER BY MAX(o.issueDate) DESC
""")
   List<Object[]> findTopClientsByRecentOrders(Pageable pageable);
   List<client> findBySenderId(String senderId);
   List<client> findByBuyerIdentifier(String buyerIdentifier);// This returns a list of clients
   Optional<client> findById(Long  Id);
   List<client> findBySenderIdContainingIgnoreCase(String senderId);
   List<client> findByBuyerIdentifierContainingIgnoreCase(String buyerIdentifier);
   @Query("SELECT c.id, c.buyerIdentifier, c.senderId, COUNT(o.id) AS orderCount " +
           "FROM client c " +
           "JOIN c.orders o " +
           "WHERE SUBSTRING(o.issueDate, 1, 7) = :monthYear " + // Extract "yyyy-MM"
           "GROUP BY c.id, c.buyerIdentifier, c.senderId " +
           "ORDER BY orderCount DESC")
   List<Object[]> findTopClientsByOrderCountThisMonth(String monthYear);

   @Query("SELECT c.id, c.buyerIdentifier, c.senderId, COUNT(o.id) AS orderCount " +
           "FROM client c " +
           "JOIN c.orders o " +
           "WHERE SUBSTRING(o.issueDate, 1, 4) = :year " + // Extract "yyyy"
           "GROUP BY c.id, c.buyerIdentifier, c.senderId " +
           "ORDER BY orderCount DESC")
   List<Object[]> findTopClientsByOrderCountThisYear(String year);
   @Query("SELECT c.id, c.buyerIdentifier, c.senderId, COUNT(o.id) AS orderCount " +
           "FROM client c " +
           "JOIN c.orders o " +
           "WHERE o.issueDate BETWEEN :startOfWeek AND :endOfWeek " +
           "GROUP BY c.id, c.buyerIdentifier, c.senderId " +
           "ORDER BY orderCount DESC")
   List<Object[]> findTopClientsByOrderCountThisWeek(@Param("startOfWeek") String startOfWeek,
                                                     @Param("endOfWeek") String endOfWeek);
   @Query("SELECT c.id, c.buyerIdentifier, c.senderId, COUNT(o.id) AS orderCount " +
           "FROM client c " +
           "JOIN c.orders o " +
           "WHERE SUBSTRING(o.issueDate, 1, 10) = :today " + // Only match the date part
           "GROUP BY c.id, c.buyerIdentifier, c.senderId " +
           "ORDER BY orderCount DESC")
   List<Object[]> findTop3ClientsByOrderCountToday(@Param("today") String today, Pageable pageable);

   @Query("""
    SELECT COUNT(c)
    FROM client c
    WHERE (
        SELECT COUNT(o)
        FROM c.orders o
        WHERE SUBSTRING(o.issueDate, 1, 7) = :monthYear
    ) > 10
""")

   Long countActiveClientsThisMonth(@Param("monthYear") String monthYear);

   @Query("""
    SELECT COUNT(c)
    FROM client c
    WHERE (
        SELECT COUNT(o)
        FROM c.orders o
        WHERE SUBSTRING(o.issueDate, 1, 7) = :monthYear
    ) < 10
""")
   Long countInactiveClientsThisMonth(@Param("monthYear") String monthYear);

}


