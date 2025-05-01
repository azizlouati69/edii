package com.example.Edi_dash.controller;

import com.example.Edi_dash.DTO.orderdto;
import entities.model.order;

import com.example.Edi_dash.sevice.orderservice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
@CrossOrigin(origins = "http://localhost:4200")

@RestController
@RequestMapping("/orders/")
public class ordercontroller {
    private final orderservice orderService;

    public ordercontroller(orderservice orderService) {
        this.orderService = orderService;
    }
    @GetMapping
    public List<order> getAllOrders() {
        return orderService.getAllOrders();
    }
    @GetMapping("/DocumentId/{documentid}")
    public order getOrderByDocumentId(@PathVariable String documentid) {
        return   orderService.getOrderByDocumentId(documentid);
    }
    @GetMapping("/this-year")
    public Long getOrdersThisYear() {
        return orderService.getTotalOrdersThisYear();
    }
    @GetMapping("/top3-buyer-articles-this-month")
    public ResponseEntity<List<Map<String, Object>>> getTop3BuyerArticlesThisMonth() {
        return ResponseEntity.ok(orderService.getTop3BuyerArticlesThisMonth());
    }
    @GetMapping("/top3/year")
    public List<Map<String, Object>> getTop3ThisYear() {
        return orderService.getTop3BuyerArticlesThisYear();
    }

    @GetMapping("/top3/today")
    public List<Map<String, Object>> getTop3Today() {
        return orderService.getTop3BuyerArticlesToday();
    }

    @GetMapping("/top3/week")
    public List<Map<String, Object>> getTop3ThisWeek() {
        return orderService.getTop3BuyerArticlesThisWeek();
    }
    @GetMapping("/top-3-articles")
    public ResponseEntity<List<Map<String, Object>>> getTop3BuyerArticles() {
        return ResponseEntity.ok(orderService.getTop3BuyerArticles());
    }
    @GetMapping("/tweek")
    public ResponseEntity<List<order>> getOrdersForThisWeek() {
        List<order> orders = orderService.getOrdersForThisWeek();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/tmonth")
    public ResponseEntity<List<order>> getOrdersForThisMonth() {
        List<order> orders = orderService.getOrdersForThisMonth();
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/top5")
    public List<orderdto> getTop3Orders() {
        return orderService.getTop5LatestOrders();
    }
    @GetMapping("/tyear")
    public ResponseEntity<List<order>> getOrdersForThisYear() {
        List<order> orders = orderService.getOrdersForThisYear();
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/ttoday")
    public ResponseEntity<List<order>> getOrdersForToday() {
        List<order> orders = orderService.getOrdersForToday();
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/this-week")
    public Long getOrdersThisWeek() {
        return orderService.getOrdersThisWeek();
    }

    @GetMapping("/order-growth-trend")
    public Map<String, Long> getOrderGrowthTrend() {
        return orderService.getMonthlyOrderCounts();
    }

    @GetMapping("/this-month")
    public ResponseEntity<Long> getOrdersThisMonth() {
        return ResponseEntity.ok(orderService.getOrdersThisMonth());
    }
    @GetMapping("/today")
    public ResponseEntity<Long> getOrdersToday() {
        return ResponseEntity.ok(orderService.getOrdersToday());
    }

    @GetMapping("/total")
    public ResponseEntity<Long> getTotalOrders() {
        return ResponseEntity.ok(orderService.getTotalOrders());
    }
    @GetMapping("/search/DocumentId")
    public List<order> searchByDocumentId(@RequestParam String DocumentId) {
        return orderService.searchByDocumentId(DocumentId);
    }
    @GetMapping("/search/DocumentNumber")
    public List<order> searchByDocumentNumber(@RequestParam String DocumentNumber) {
        return orderService.searchByDocumentNumber(DocumentNumber);
    }
    @GetMapping("/search/Description")
    public List<order> searchByDescription(@RequestParam String Description) {
        return orderService.searchByDescription(Description);
    }
    @GetMapping("/search/BuyerArticleNumber")
    public List<order> searchByBuyerArticleNumber(@RequestParam String BuyerArticleNumber) {
        return orderService.searchByBuyerArticleNumber(BuyerArticleNumber);
    }
    @GetMapping("/search/IssueDate")
    public List<order> searchByIssueDate(@RequestParam String IssueDate) {
        return orderService.searchByIssueDate(IssueDate);
    }
    @GetMapping("/search/CalculationDate")
    public List<order> searchByCalculationDate(@RequestParam String CalculationDate) {
        return orderService.searchByCalculationDate(CalculationDate);
    }
    @GetMapping("/search/Shipto")
    public List<order> searchByShipto(@RequestParam String Shipto) {
        return orderService.searchByShipto(Shipto);
    }
    @GetMapping("/search/Internaldestination")
    public List<order> searchByInternaldestination(@RequestParam String Internaldestination) {
        return orderService.searchByInternaldestination(Internaldestination);
    }
    @GetMapping("/search/Placeofdischarge")
    public List<order> searchByPlaceofdischarge(@RequestParam String Placeofdischarge) {
        return orderService.searchByPlaceofdischarge(Placeofdischarge);
    }
    @GetMapping("/Id/{Id}")
    public Optional<order> getOrderById(@PathVariable Long Id) {
        return orderService.getOrderById(Id);
    }
}
