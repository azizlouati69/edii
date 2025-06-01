package com.example.EdiASN.service;


import com.example.EdiASN.dto.InvoiceArticleRequest;
import com.example.EdiASN.entity.Article;
import com.example.EdiASN.entity.Invoice;
import com.example.EdiASN.entity.InvoiceArticle;
import com.example.EdiASN.repository.ArticleRepository;
import com.example.EdiASN.repository.InvoiceRepository;
import com.example.EdiASN.repository.InvoiceArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceArticleService {

    private final ArticleRepository articleRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceArticleRepository articleInvoiceRepository;

    public InvoiceArticleService(ArticleRepository articleRepository, InvoiceRepository invoiceRepository, InvoiceArticleRepository articleInvoiceRepository) {
        this.articleRepository = articleRepository;
        this.invoiceRepository = invoiceRepository;
        this.articleInvoiceRepository = articleInvoiceRepository;
    }

    public InvoiceArticle createInvoiceArticleg(Long articleId, Long invoiceId, int quantity , Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        InvoiceArticle articleInvoice = new InvoiceArticle();
        articleInvoice.setArticle(article);
        articleInvoice.setInvoice(invoice);
        articleInvoice.setTotalQuantity(quantity);
        articleInvoice.setUserId(userId);
        return articleInvoiceRepository.save(articleInvoice);
    }
    public InvoiceArticle createInvoiceArticle(Long articleId, Long invoiceId, int quantity, Long currentUserId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        // Check ownership
        if (!article.getUserId().equals(currentUserId)) {
            throw new RuntimeException("You do not own this article");
        }
        if (!invoice.getUserId().equals(currentUserId)) {
            throw new RuntimeException("You do not own this invoice");
        }

        InvoiceArticle articleInvoice = new InvoiceArticle();
        articleInvoice.setArticle(article);
        articleInvoice.setInvoice(invoice);
        articleInvoice.setTotalQuantity(quantity);
        articleInvoice.setUserId(currentUserId);
        return articleInvoiceRepository.save(articleInvoice);
    }

    public List<InvoiceArticle> getAll() {
        return articleInvoiceRepository.findAll();
    }

    public List<InvoiceArticle> getAllByUserId(Long userId) {
        return articleInvoiceRepository.findByUserId(userId);
    }

    public InvoiceArticle getById(Long id) {
        return articleInvoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }
    public InvoiceArticle getByIdForUser(Long id, Long userId) {
        return articleInvoiceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Invoice not found or not authorized"));
    }


    public InvoiceArticle update(Long id, InvoiceArticleRequest request) {
        InvoiceArticle ac = getById(id);

        if (request.getArticleId() != null) {
            Article article = articleRepository.findById(request.getArticleId())
                    .orElseThrow(() -> new RuntimeException("Article not found"));
            ac.setArticle(article);
        }

        if (request.getInvoiceId() != null) {
            Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));
            ac.setInvoice(invoice);
        }

        if (request.getTotalQuantity() != null) {
            ac.setTotalQuantity(request.getTotalQuantity());
        }

        return articleInvoiceRepository.save(ac);
    }
    public InvoiceArticle getInvoiceById(Long id, Long userId) {
        return articleInvoiceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Invoice not found or not authorized"));
    }
    public InvoiceArticle updateForUser(Long id, InvoiceArticleRequest request, Long userId) {
        InvoiceArticle ac = getInvoiceById(id, userId);
        if (request.getArticleId() != null) {
            Article article = articleRepository.findById(request.getArticleId())
                    .orElseThrow(() -> new RuntimeException("Article not found"));
            ac.setArticle(article);
        }

        if (request.getInvoiceId() != null) {
            Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));
            ac.setInvoice(invoice);
        }

        if (request.getTotalQuantity() != null) {
            ac.setTotalQuantity(request.getTotalQuantity());
        }

        return articleInvoiceRepository.save(ac);
    }

    public void delete(Long id) {
        InvoiceArticle ac = getById(id);
        articleInvoiceRepository.delete(ac);
    }

    public void deleteForUser(Long id, Long userId) {
        InvoiceArticle InvoiceArticle = getInvoiceById(id, userId);
        articleInvoiceRepository.delete(InvoiceArticle);
    }

}
