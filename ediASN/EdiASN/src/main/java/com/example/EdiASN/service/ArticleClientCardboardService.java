package com.example.EdiASN.service;

import com.example.EdiASN.dto.ArticleClientCardboardRequest;
import com.example.EdiASN.entity.ArticleClient;
import com.example.EdiASN.entity.Cardboard;
import com.example.EdiASN.entity.ArticleClientCardboard;
import com.example.EdiASN.repository.ArticleClientRepository;
import com.example.EdiASN.repository.CardboardRepository;
import com.example.EdiASN.repository.ArticleClientCardboardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleClientCardboardService {

    private final ArticleClientRepository articleclientRepository;
    private final CardboardRepository cardboardRepository;
    private final ArticleClientCardboardRepository articleclientCardboardRepository;

    public ArticleClientCardboardService(ArticleClientRepository articleclientRepository, CardboardRepository cardboardRepository, ArticleClientCardboardRepository articleclientCardboardRepository) {
        this.articleclientRepository = articleclientRepository;
        this.cardboardRepository = cardboardRepository;
        this.articleclientCardboardRepository = articleclientCardboardRepository;
    }

    public ArticleClientCardboard createArticleClientCardboardg(Long articleclientId, Long cardboardId,Long default_cardboardId, int quantity , Long userId) {
        ArticleClient articleclient = articleclientRepository.findById(articleclientId)
                .orElseThrow(() -> new RuntimeException("ArticleClient not found"));
        Cardboard cardboard = cardboardRepository.findById(cardboardId)
                .orElseThrow(() -> new RuntimeException("Cardboard not found"));
        Cardboard dcardboard = cardboardRepository.findById(default_cardboardId)
                .orElseThrow(() -> new RuntimeException(" Default Cardboard not found"));

        ArticleClientCardboard articleclientCardboard = new ArticleClientCardboard();
        articleclientCardboard.setArticleClient(articleclient);
        articleclientCardboard.setCardboard(cardboard);
        articleclientCardboard.setDefault_cardboard(dcardboard);
        articleclientCardboard.setQuantityPerCardboard(quantity);
        articleclientCardboard.setUserId(userId);
        return articleclientCardboardRepository.save(articleclientCardboard);
    }
    public ArticleClientCardboard createArticleClientCardboard(Long articleclientId, Long cardboardId, Long dcardboardId, int quantity, Long currentUserId) {
        ArticleClient articleclient = articleclientRepository.findById(articleclientId)
                .orElseThrow(() -> new RuntimeException("ArticleClient not found"));

        Cardboard cardboard = cardboardRepository.findById(cardboardId)
                .orElseThrow(() -> new RuntimeException("Cardboard not found"));
        Cardboard dcardboard = cardboardRepository.findById(dcardboardId)
                .orElseThrow(() -> new RuntimeException(" Default Cardboard not found"));
        // Check ownership
        if (!articleclient.getUserId().equals(currentUserId)) {
            throw new RuntimeException("You do not own this articleclient");
        }
        if (!cardboard.getUserId().equals(currentUserId)) {
            throw new RuntimeException("You do not own this cardboard");
        }
        if (!dcardboard.getUserId().equals(currentUserId)) {
            throw new RuntimeException("You do not own this default cardboard");
        }

        ArticleClientCardboard articleclientCardboard = new ArticleClientCardboard();
        articleclientCardboard.setArticleClient(articleclient);
        articleclientCardboard.setCardboard(cardboard);
        articleclientCardboard.setDefault_cardboard(dcardboard);
        articleclientCardboard.setQuantityPerCardboard(quantity);
        articleclientCardboard.setUserId(currentUserId);
        return articleclientCardboardRepository.save(articleclientCardboard);
    }

    public List<ArticleClientCardboard> getAll() {
        return articleclientCardboardRepository.findAll();
    }

    public List<ArticleClientCardboard> getAllByUserId(Long userId) {
        return articleclientCardboardRepository.findByUserId(userId);
    }

    public ArticleClientCardboard getById(Long id) {
        return articleclientCardboardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }
    public ArticleClientCardboard getByIdForUser(Long id, Long userId) {
        return articleclientCardboardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException(" not found or not authorized"));
    }


    public ArticleClientCardboard update(Long id, ArticleClientCardboardRequest request) {
        ArticleClientCardboard ac = getById(id);

        if (request.getArticleClientId() != null) {
            ArticleClient articleclient = articleclientRepository.findById(request.getArticleClientId())
                    .orElseThrow(() -> new RuntimeException("ArticleClient not found"));
            ac.setArticleClient(articleclient);
        }

        if (request.getCardboardId() != null) {
            Cardboard cardboard = cardboardRepository.findById(request.getCardboardId())
                    .orElseThrow(() -> new RuntimeException("Cardboard not found"));
            ac.setCardboard(cardboard);
        }
        if (request.getDefault_cardboardId() != null) {
            Cardboard dcardboard = cardboardRepository.findById(request.getDefault_cardboardId())
                    .orElseThrow(() -> new RuntimeException(" Default Cardboard not found"));
            ac.setDefault_cardboard(dcardboard);
        }

        if (request.getQuantityPerCardboard() != null) {
            ac.setQuantityPerCardboard(request.getQuantityPerCardboard());
        }

        return articleclientCardboardRepository.save(ac);
    }
    public ArticleClientCardboard getArticleClientCardboardById(Long id, Long userId) {
        return articleclientCardboardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Cardboard not found or not authorized"));
    }
    public ArticleClientCardboard updateForUser(Long id, ArticleClientCardboardRequest request, Long userId) {
        ArticleClientCardboard ac = getArticleClientCardboardById(id, userId);
        if (request.getArticleClientId() != null) {
            ArticleClient articleclient = articleclientRepository.findById(request.getArticleClientId())
                    .orElseThrow(() -> new RuntimeException("ArticleClient not found"));
            ac.setArticleClient(articleclient);
        }

        if (request.getCardboardId() != null) {
            Cardboard cardboard = cardboardRepository.findById(request.getCardboardId())
                    .orElseThrow(() -> new RuntimeException("Cardboard not found"));
            ac.setCardboard(cardboard);
        }
        if (request.getDefault_cardboardId() != null) {
            Cardboard dcardboard = cardboardRepository.findById(request.getDefault_cardboardId())
                    .orElseThrow(() -> new RuntimeException(" Default Cardboard not found"));
            ac.setCardboard(dcardboard);
        }

        if (request.getQuantityPerCardboard() != null) {
            ac.setQuantityPerCardboard(request.getQuantityPerCardboard());
        }

        return articleclientCardboardRepository.save(ac);
    }

    public void delete(Long id) {
        ArticleClientCardboard ac = getById(id);
        articleclientCardboardRepository.delete(ac);
    }

    public void deleteForUser(Long id, Long userId) {
        ArticleClientCardboard ArticleClientCardboard = getArticleClientCardboardById(id, userId);
        articleclientCardboardRepository.delete(ArticleClientCardboard);
    }

}
