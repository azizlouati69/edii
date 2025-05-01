package  repository;

import entities.model.client;
import entities.model.order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface orderrepository extends JpaRepository<order, Long> {
    List<order> findByDocumentId(String documentId);  // This returns a list of clients
    Optional<order> findById(Long  Id);
    List<order> findByDocumentIdContainingIgnoreCase(String documentId);
    List<order> findByDescriptionContainingIgnoreCase(String description);
    List<order> findByBuyerArticleNumberContainingIgnoreCase(String buyerArticleNumber);
    List<order> findByDocumentNumberContainingIgnoreCase(String documentNumber);
    List<order> findByIssueDateContainingIgnoreCase(String issueDate);
    List<order> findByCalculationDateContainingIgnoreCase(String calculationDate);
    List<order> findByShiptoContainingIgnoreCase(String shipto);
    List<order> findByInternaldestinationContainingIgnoreCase(String internaldestination);
    List<order> findByPlaceofdischargeContainingIgnoreCase(String placeofdischarge);
    List<order> findByClient(client client);

    @Query("SELECT COUNT(o) FROM order o")
    Long getTotalOrders();
    @Query("""
    SELECT o.id      ,\s
           o.documentId ,\s
           o.description ,\s
           o.buyerArticleNumber,\s
           o.documentNumber ,\s
           MAX(o.issueDate)\s
    FROM order o
    GROUP BY o.id, o.documentId, o.description, o.buyerArticleNumber, o.documentNumber
    ORDER BY MAX(o.issueDate) DESC
""")
    List<Object[]> findTop3Orders(Pageable pageable);
    @Query("SELECT COUNT(o) FROM order o WHERE SUBSTRING(o.issueDate, 1, 4) = :year")
    Long getTotalOrdersThisYear(@Param("year") String year);
    @Query("SELECT o.buyerArticleNumber, COUNT(o.buyerArticleNumber) " +
            "FROM order o GROUP BY o.buyerArticleNumber " +
            "ORDER BY COUNT(o.buyerArticleNumber) DESC")
    List<Object[]> findTop3MostFrequentBuyerArticles(Pageable pageable);
    @Query("SELECT o FROM order o WHERE o.issueDate >= :startDate AND o.issueDate < :endDate")
    List<order> findOrdersBetween(@Param("startDate") String startDate, @Param("endDate") String endDate);
    @Query("SELECT SUBSTRING(o.issueDate, 1, 7) AS month, COUNT(o) AS total " +
            "FROM order o " +
            "GROUP BY SUBSTRING(o.issueDate, 1, 7) " +
            "ORDER BY month ASC")
    List<Object[]> getMonthlyOrderCounts();
    @Query("SELECT o.buyerArticleNumber, COUNT(o.buyerArticleNumber) " +
            "FROM order o " +
            "WHERE SUBSTRING(o.issueDate, 1, 7) = :monthYear " + // Filter orders for the current month
            "GROUP BY o.buyerArticleNumber " +
            "ORDER BY COUNT(o.buyerArticleNumber) DESC")
    List<Object[]> findTop3MostFrequentBuyerArticles(@Param("monthYear") String monthYear, Pageable pageable);
    @Query("SELECT o.buyerArticleNumber, COUNT(o.buyerArticleNumber) " +
            "FROM order o " +
            "WHERE SUBSTRING(o.issueDate, 1, 4) = :year " +
            "GROUP BY o.buyerArticleNumber " +
            "ORDER BY COUNT(o.buyerArticleNumber) DESC")
    List<Object[]> findTop3MostFrequentBuyerArticlesThisYear(@Param("year") String year, Pageable pageable);
    @Query("SELECT o.buyerArticleNumber, COUNT(o.buyerArticleNumber) " +
            "FROM order o " +
            "WHERE SUBSTRING(o.issueDate, 1, 10) = :today " +
            "GROUP BY o.buyerArticleNumber " +
            "ORDER BY COUNT(o.buyerArticleNumber) DESC")
    List<Object[]> findTop3MostFrequentBuyerArticlesToday(@Param("today") String today, Pageable pageable);
    @Query("SELECT o.buyerArticleNumber, COUNT(o.buyerArticleNumber) " +
            "FROM order o " +
            "WHERE SUBSTRING(o.issueDate, 1, 10) IN :weekDates " +
            "GROUP BY o.buyerArticleNumber " +
            "ORDER BY COUNT(o.buyerArticleNumber) DESC")
    List<Object[]> findTop3MostFrequentBuyerArticlesThisWeek(@Param("weekDates") List<String> weekDates, Pageable pageable);


}






