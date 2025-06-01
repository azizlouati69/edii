package com.example.EdiASN.service;

import com.example.EdiASN.dto.ArticleCardboardRequest;
import com.example.EdiASN.entity.Article;
import com.example.EdiASN.entity.Cardboard;
import com.example.EdiASN.entity.ArticleCardboard;
import com.example.EdiASN.repository.ArticleRepository;
import com.example.EdiASN.repository.CardboardRepository;
import com.example.EdiASN.repository.ArticleCardboardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleCardboardService {

    private final ArticleRepository articleRepository;
    private final CardboardRepository cardboardRepository;
    private final ArticleCardboardRepository articleCardboardRepository;

    public ArticleCardboardService(ArticleRepository articleRepository, CardboardRepository cardboardRepository, ArticleCardboardRepository articleCardboardRepository) {
        this.articleRepository = articleRepository;
        this.cardboardRepository = cardboardRepository;
        this.articleCardboardRepository = articleCardboardRepository;
    }

    public ArticleCardboard createArticleCardboardg(Long articleId, Long cardboardId, int quantity , Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        Cardboard cardboard = cardboardRepository.findById(cardboardId)
                .orElseThrow(() -> new RuntimeException("Cardboard not found"));

        ArticleCardboard articleCardboard = new ArticleCardboard();
        articleCardboard.setArticle(article);
        articleCardboard.setCardboard(cardboard);
        articleCardboard.setQuantityPerCardboard(quantity);
        articleCardboard.setUserId(userId);
        return articleCardboardRepository.save(articleCardboard);
    }
    public ArticleCardboard createArticleCardboard(Long articleId, Long cardboardId, int quantity, Long currentUserId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        Cardboard cardboard = cardboardRepository.findById(cardboardId)
                .orElseThrow(() -> new RuntimeException("Cardboard not found"));

        // Check ownership
        if (!article.getUserId().equals(currentUserId)) {
            throw new RuntimeException("You do not own this article");
        }
        if (!cardboard.getUserId().equals(currentUserId)) {
            throw new RuntimeException("You do not own this cardboard");
        }

        ArticleCardboard articleCardboard = new ArticleCardboard();
        articleCardboard.setArticle(article);
        articleCardboard.setCardboard(cardboard);
        articleCardboard.setQuantityPerCardboard(quantity);
        articleCardboard.setUserId(currentUserId);
        return articleCardboardRepository.save(articleCardboard);
    }

    public List<ArticleCardboard> getAll() {
        return articleCardboardRepository.findAll();
    }

    public List<ArticleCardboard> getAllByUserId(Long userId) {
        return articleCardboardRepository.findByUserId(userId);
    }

    public ArticleCardboard getById(Long id) {
        return articleCardboardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }
    public ArticleCardboard getByIdForUser(Long id, Long userId) {
        return articleCardboardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Cardboard not found or not authorized"));
    }


    public ArticleCardboard update(Long id, ArticleCardboardRequest request) {
        ArticleCardboard ac = getById(id);

        if (request.getArticleId() != null) {
            Article article = articleRepository.findById(request.getArticleId())
                    .orElseThrow(() -> new RuntimeException("Article not found"));
            ac.setArticle(article);
        }

        if (request.getCardboardId() != null) {
            Cardboard cardboard = cardboardRepository.findById(request.getCardboardId())
                    .orElseThrow(() -> new RuntimeException("Cardboard not found"));
            ac.setCardboard(cardboard);
        }

        if (request.getQuantityPerCardboard() != null) {
            ac.setQuantityPerCardboard(request.getQuantityPerCardboard());
        }

        return articleCardboardRepository.save(ac);
    }
    public ArticleCardboard getCardboardById(Long id, Long userId) {
        return articleCardboardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Cardboard not found or not authorized"));
    }
    public ArticleCardboard updateForUser(Long id, ArticleCardboardRequest request, Long userId) {
        ArticleCardboard ac = getCardboardById(id, userId);
        if (request.getArticleId() != null) {
            Article article = articleRepository.findById(request.getArticleId())
                    .orElseThrow(() -> new RuntimeException("Article not found"));
            ac.setArticle(article);
        }

        if (request.getCardboardId() != null) {
            Cardboard cardboard = cardboardRepository.findById(request.getCardboardId())
                    .orElseThrow(() -> new RuntimeException("Cardboard not found"));
            ac.setCardboard(cardboard);
        }

        if (request.getQuantityPerCardboard() != null) {
            ac.setQuantityPerCardboard(request.getQuantityPerCardboard());
        }

        return articleCardboardRepository.save(ac);
    }

    public void delete(Long id) {
        ArticleCardboard ac = getById(id);
        articleCardboardRepository.delete(ac);
    }

    public void deleteForUser(Long id, Long userId) {
        ArticleCardboard ArticleCardboard = getCardboardById(id, userId);
        articleCardboardRepository.delete(ArticleCardboard);
    }

}
