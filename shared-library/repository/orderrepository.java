package repository;

import entities.model.client;
import entities.model.order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface orderrepository extends JpaRepository<order, Long> {
    @Query(value = "SELECT o FROM order o WHERE CAST(FUNCTION('DATE_FORMAT', o.issueDate, '%Y-%m-%d') AS string) LIKE %:dateString%")
    List<order> findByIssueDateContainingStringMySQL(@Param("dateString") String dateString);

    List<order> findByDocumentId(String documentId);
    Optional<order> findById(Long id);
    List<order> findByDocumentIdContainingIgnoreCase(String documentId);
    List<order> findByDescriptionContainingIgnoreCase(String description);
    List<order> findByBuyerArticleNumberContainingIgnoreCase(String buyerArticleNumber);
    List<order> findByDocumentNumberContainingIgnoreCase(String documentNumber);
    List<order> findByShiptoContainingIgnoreCase(String shipto);
    List<order> findByInternaldestinationContainingIgnoreCase(String internaldestination);
    List<order> findByPlaceofdischargeContainingIgnoreCase(String placeofdischarge);
    List<order> findByClient(client client);

    // Use this instead of issueDate as String
    List<order> findByIssueDate(LocalDate issueDate);
    List<order> findByCalculationDate(LocalDate calculationDate);

    @Query("SELECT COUNT(o) FROM order o")
    Long getTotalOrders();

    @Query("""
        SELECT o.id, o.documentId, o.description, o.buyerArticleNumber, o.documentNumber, MAX(o.issueDate)
        FROM order o
        GROUP BY o.id, o.documentId, o.description, o.buyerArticleNumber, o.documentNumber
        ORDER BY MAX(o.issueDate) DESC
    """)
    List<Object[]> findTop3Orders(Pageable pageable);

    @Query("SELECT COUNT(o) FROM order o WHERE YEAR(o.issueDate) = :year")
    Long getTotalOrdersThisYear(@Param("year") int year);

    @Query("""
        SELECT o.buyerArticleNumber, COUNT(o.buyerArticleNumber)
        FROM order o
        GROUP BY o.buyerArticleNumber
        ORDER BY COUNT(o.buyerArticleNumber) DESC
    """)
    List<Object[]> findTop3MostFrequentBuyerArticles(Pageable pageable);

    @Query("""
        SELECT o FROM order o
        WHERE o.issueDate >= :startDate AND o.issueDate <= :endDate
    """)
    List<order> findOrdersBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT FUNCTION('DATE_FORMAT', o.issueDate, '%Y-%m') AS month, COUNT(o) AS total
        FROM order o
        GROUP BY FUNCTION('DATE_FORMAT', o.issueDate, '%Y-%m')
        ORDER BY month ASC
    """)
    List<Object[]> getMonthlyOrderCounts();

    @Query("""
        SELECT o.buyerArticleNumber, COUNT(o.buyerArticleNumber)
        FROM order o
        WHERE o.issueDate BETWEEN :startDate AND :endDate
        GROUP BY o.buyerArticleNumber
        ORDER BY COUNT(o.buyerArticleNumber) DESC
    """)
    List<Object[]> findTop3MostFrequentBuyerArticlesThisMonth(@Param("startDate") LocalDate startDate,
                                                              @Param("endDate") LocalDate endDate,
                                                              Pageable pageable);

    @Query("""
        SELECT o.buyerArticleNumber, COUNT(o.buyerArticleNumber)
        FROM order o
        WHERE YEAR(o.issueDate) = :year
        GROUP BY o.buyerArticleNumber
        ORDER BY COUNT(o.buyerArticleNumber) DESC
    """)
    List<Object[]> findTop3MostFrequentBuyerArticlesThisYear(@Param("year") int year, Pageable pageable);

    @Query("""
        SELECT o.buyerArticleNumber, COUNT(o.buyerArticleNumber)
        FROM order o
        WHERE o.issueDate = :today
        GROUP BY o.buyerArticleNumber
        ORDER BY COUNT(o.buyerArticleNumber) DESC
    """)
    List<Object[]> findTop3MostFrequentBuyerArticlesToday(@Param("today") LocalDate today, Pageable pageable);

    @Query("""
        SELECT o.buyerArticleNumber, COUNT(o.buyerArticleNumber)
        FROM order o
        WHERE o.issueDate IN :weekDates
        GROUP BY o.buyerArticleNumber
        ORDER BY COUNT(o.buyerArticleNumber) DESC
    """)
    List<Object[]> findTop3MostFrequentBuyerArticlesThisWeek(@Param("weekDates") List<LocalDate> weekDates, Pageable pageable);
}
