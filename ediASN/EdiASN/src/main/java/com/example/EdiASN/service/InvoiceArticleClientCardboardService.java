package com.example.EdiASN.service;


import com.example.EdiASN.dto.InvoiceArticleclientcardboardRequest;
import com.example.EdiASN.dto.InvoiceArticleclientcardboardRequest;
import com.example.EdiASN.entity.ArticleClientCardboard;
import com.example.EdiASN.entity.Invoice;
import com.example.EdiASN.entity.InvoiceArticleClientCardboard;
import com.example.EdiASN.repository.ArticleClientCardboardRepository;
import com.example.EdiASN.repository.InvoiceRepository;
import com.example.EdiASN.repository.InvoiceArticleClientCardboardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceArticleClientCardboardService {

    private final ArticleClientCardboardRepository articleRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceArticleClientCardboardRepository invoicearticlelientcardboardRepository;

    public InvoiceArticleClientCardboardService(ArticleClientCardboardRepository articleRepository, InvoiceRepository invoiceRepository, InvoiceArticleClientCardboardRepository invoicearticlelientcardboardRepository) {
        this.articleRepository = articleRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoicearticlelientcardboardRepository = invoicearticlelientcardboardRepository;
    }

    public InvoiceArticleClientCardboard createInvoiceArticleClientCardboardg(Long articleId, Long invoiceId, int quantity , Long userId) {
        ArticleClientCardboard article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("ArticleClientCardboard not found"));
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        InvoiceArticleClientCardboard invoicearticlelientcardboard = new InvoiceArticleClientCardboard();
        invoicearticlelientcardboard.setArticleClientCardboard(article);
        invoicearticlelientcardboard.setInvoice(invoice);
        invoicearticlelientcardboard.setTotalQuantity(quantity);
        invoicearticlelientcardboard.setUserId(userId);
        return invoicearticlelientcardboardRepository.save(invoicearticlelientcardboard);
    }
    public InvoiceArticleClientCardboard createInvoiceArticleClientCardboard(Long articleId, Long invoiceId, int quantity, Long currentUserId) {
        ArticleClientCardboard article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("ArticleClientCardboard not found"));

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        // Check ownership
        if (!article.getUserId().equals(currentUserId)) {
            throw new RuntimeException("You do not own this article");
        }
        if (!invoice.getUserId().equals(currentUserId)) {
            throw new RuntimeException("You do not own this invoice");
        }

        InvoiceArticleClientCardboard invoicearticlelientcardboard = new InvoiceArticleClientCardboard();
        invoicearticlelientcardboard.setArticleClientCardboard(article);
        invoicearticlelientcardboard.setInvoice(invoice);
        invoicearticlelientcardboard.setTotalQuantity(quantity);
        invoicearticlelientcardboard.setUserId(currentUserId);
        return invoicearticlelientcardboardRepository.save(invoicearticlelientcardboard);
    }

    public List<InvoiceArticleClientCardboard> getAll() {
        return invoicearticlelientcardboardRepository.findAll();
    }

    public List<InvoiceArticleClientCardboard> getAllByUserId(Long userId) {
        return invoicearticlelientcardboardRepository.findByUserId(userId);
    }

    public InvoiceArticleClientCardboard getById(Long id) {
        return invoicearticlelientcardboardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }
    public InvoiceArticleClientCardboard getByIdForUser(Long id, Long userId) {
        return invoicearticlelientcardboardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Invoice not found or not authorized"));
    }


    public InvoiceArticleClientCardboard update(Long id, InvoiceArticleclientcardboardRequest request) {
        InvoiceArticleClientCardboard ac = getById(id);

        if (request.getArticleclientcardboardId() != null) {
            ArticleClientCardboard article = articleRepository.findById(request.getArticleclientcardboardId())
                    .orElseThrow(() -> new RuntimeException("ArticleClientCardboard not found"));
            ac.setArticleClientCardboard(article);
        }

        if (request.getInvoiceId() != null) {
            Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));
            ac.setInvoice(invoice);
        }

        if (request.getTotalQuantity() != null) {
            ac.setTotalQuantity(request.getTotalQuantity());
        }

        return invoicearticlelientcardboardRepository.save(ac);
    }
    public InvoiceArticleClientCardboard getInvoiceById(Long id, Long userId) {
        return invoicearticlelientcardboardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Invoice not found or not authorized"));
    }
    public InvoiceArticleClientCardboard updateForUser(Long id, InvoiceArticleclientcardboardRequest request, Long userId) {
        InvoiceArticleClientCardboard ac = getInvoiceById(id, userId);
        if (request.getArticleclientcardboardId() != null) {
            ArticleClientCardboard article = articleRepository.findById(request.getArticleclientcardboardId())
                    .orElseThrow(() -> new RuntimeException("ArticleClientCardboard not found"));
            ac.setArticleClientCardboard(article);
        }

        if (request.getInvoiceId() != null) {
            Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));
            ac.setInvoice(invoice);
        }

        if (request.getTotalQuantity() != null) {
            ac.setTotalQuantity(request.getTotalQuantity());
        }

        return invoicearticlelientcardboardRepository.save(ac);
    }

    public void delete(Long id) {
        InvoiceArticleClientCardboard ac = getById(id);
        invoicearticlelientcardboardRepository.delete(ac);
    }

    public void deleteForUser(Long id, Long userId) {
        InvoiceArticleClientCardboard InvoiceArticleClientCardboard = getInvoiceById(id, userId);
        invoicearticlelientcardboardRepository.delete(InvoiceArticleClientCardboard);
    }

}
