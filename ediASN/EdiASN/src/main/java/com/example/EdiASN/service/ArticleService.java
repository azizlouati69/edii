package com.example.EdiASN.service;

import com.example.EdiASN.dto.ArticleDTO;
import com.example.EdiASN.entity.Article;
import com.example.EdiASN.entity.Cardboard;
import com.example.EdiASN.entity.Client;
import com.example.EdiASN.repository.ArticleRepository;
import com.example.EdiASN.repository.CardboardRepository;
import com.example.EdiASN.repository.ClientRepository;
import com.example.EdiASN.security.JwtService;
import com.example.EdiASN.security.JwtUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtUtils jwtUtils;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired

    private CardboardRepository cardboardRepository; // assuming you have one
    // assuming you have one
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return Long.valueOf(authentication.getName());
        }
        throw new RuntimeException("Unauthenticated");
    }

    // === USER-SPECIFIC ===

    public List<Article> getAllArticlesByUser(Long userId) {
        return articleRepository.findByUserId(userId);
    }

    public Article getArticleById(Long id, Long userId) {
        return articleRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Article not found or not authorized"));
    }

    public Article createArticle(ArticleDTO dto, Long userId) {
        Article article = new Article();
        article.setUserId(userId);
        article.setNameint(dto.getNameint());
        article.setNameext(dto.getNameext());
        article.setDesignation(dto.getDesignation());
        article.setGross_weight(dto.getGross_weight());
        article.setNet_weight(dto.getNet_weight());
        article.setLot_number(dto.getLot_number());
        article.setOrder_number(dto.getOrder_number());
        if (dto.getClientId() != null) {
            Client client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            article.setClient(client);
        }
        return articleRepository.save(article);
    }

    public Article updateArticle(Long id, ArticleDTO dto, Long userId) {
        Article article = getArticleById(id, userId);

        if (dto.getNameint() != null) article.setNameint(dto.getNameint());
        if (dto.getNameext() != null)  article.setNameext(dto.getNameext());
        if (dto.getDesignation() != null) article.setDesignation(dto.getDesignation());
        if (dto.getGross_weight() != null) article.setGross_weight(dto.getGross_weight());
        if (dto.getNet_weight() != null) article.setNet_weight(dto.getNet_weight());
        if (dto.getLot_number() != null) article.setLot_number(dto.getLot_number());
        if (dto.getOrder_number() != null) article.setOrder_number(dto.getOrder_number());
        if (dto.getClientId() != null) {
            Client client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            article.setClient(client);
        }
        return articleRepository.save(article);
    }

    public void deleteArticle(Long id, Long userId) {
        Article article = getArticleById(id, userId);
        articleRepository.delete(article);
    }

    // === ADMIN/GLOBAL ===

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public Article getArticleByIdGlobal(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
    }

    public Article updateArticleGlobal(Long id, ArticleDTO dto) {
        Article article = getArticleByIdGlobal(id);
        if (dto.getNameint() != null) article.setNameint(dto.getNameint());
        if (dto.getNameext() != null)  article.setNameext(dto.getNameext());
        if (dto.getDesignation() != null) article.setDesignation(dto.getDesignation());
        if (dto.getGross_weight() != null) article.setGross_weight(dto.getGross_weight());
        if (dto.getNet_weight() != null) article.setNet_weight(dto.getNet_weight());
        if (dto.getLot_number() != null) article.setLot_number(dto.getLot_number());
        if (dto.getOrder_number() != null) article.setOrder_number(dto.getOrder_number());
        if (dto.getClientId() != null) {
            Client client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            article.setClient(client);
        }
        return articleRepository.save(article);
    }
    public List<Article> searchArticlesByUser(ArticleDTO searchDTO, Long userId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Article> query = cb.createQuery(Article.class);
        Root<Article> root = query.from(Article.class);

        List<Predicate> predicates = new ArrayList<>();

        // User ID filter
        predicates.add(cb.equal(root.get("userId"), userId));

        // Search fields filters
        if (searchDTO.getNameint() != null) {
            predicates.add(cb.like(cb.lower(root.get("nameint")), "%" + searchDTO.getNameint().toLowerCase() + "%"));
        }
        if (searchDTO.getNameext() != null) {
            predicates.add(cb.like(cb.lower(root.get("nameext")), "%" + searchDTO.getNameext().toLowerCase() + "%"));
        }
        if (searchDTO.getDesignation() != null) {
            predicates.add(cb.like(cb.lower(root.get("designation")), "%" + searchDTO.getDesignation().toLowerCase() + "%"));
        }
        Float gw = searchDTO.getGross_weight();
        if (gw != null) {
            predicates.add(cb.between(root.get("gross_weight"), gw - 0.001f, gw + 0.001f));
        }
        Float nw = searchDTO.getNet_weight();
        if (nw != null) {
            predicates.add(cb.between(root.get("net_weight"), nw - 0.001f, nw + 0.001f));
        }
        if (searchDTO.getLot_number() != null) {
            predicates.add(cb.like(cb.lower(root.get("lot_number")), "%" + searchDTO.getLot_number().toLowerCase() + "%"));
        }
        if (searchDTO.getOrder_number() != null) {
            predicates.add(cb.like(cb.lower(root.get("order_number")), "%" + searchDTO.getOrder_number().toLowerCase() + "%"));
        }
        // Join with Client entity to filter by client name
        if (searchDTO.getClientName() != null) {
            Join<Article, Client> clientJoin = root.join("client", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(clientJoin.get("name")), "%" + searchDTO.getClientName().toLowerCase() + "%"));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Article> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    // --- ADMIN SEARCH (no userId filter) ---
    public List<Article> searchArticlesGlobal(ArticleDTO searchDTO) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Article> query = cb.createQuery(Article.class);
        Root<Article> root = query.from(Article.class);

        List<Predicate> predicates = new ArrayList<>();

        if (searchDTO.getNameint() != null) {
            predicates.add(cb.like(cb.lower(root.get("nameint")), "%" + searchDTO.getNameint().toLowerCase() + "%"));
        }
        if (searchDTO.getNameext() != null) {
            predicates.add(cb.like(cb.lower(root.get("nameext")), "%" + searchDTO.getNameext().toLowerCase() + "%"));
        }
        if (searchDTO.getDesignation() != null) {
            predicates.add(cb.like(cb.lower(root.get("designation")), "%" + searchDTO.getDesignation().toLowerCase() + "%"));
        }
        Float gw = searchDTO.getGross_weight();
        if (gw != null) {
            predicates.add(cb.between(root.get("gross_weight"), gw - 0.001f, gw + 0.001f));
        }
        Float nw = searchDTO.getNet_weight();
        if (nw != null) {
            predicates.add(cb.between(root.get("net_weight"), nw - 0.001f, nw + 0.001f));
        }
        if (searchDTO.getLot_number() != null) {
            predicates.add(cb.like(cb.lower(root.get("lot_number")), "%" + searchDTO.getLot_number().toLowerCase() + "%"));
        }
        if (searchDTO.getOrder_number() != null) {
            predicates.add(cb.like(cb.lower(root.get("order_number")), "%" + searchDTO.getOrder_number().toLowerCase() + "%"));
        }
        if (searchDTO.getClientName() != null) {
            Join<Article, Client> clientJoin = root.join("client", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(clientJoin.get("name")), "%" + searchDTO.getClientName().toLowerCase() + "%"));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Article> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }
    public void deleteArticleGlobal(Long id) {
        Article article = getArticleByIdGlobal(id);
        articleRepository.delete(article);
    }
    // For the authenticated user
    public Article setDefaultCardboardByUser(Long articleId, Long cardboardId, Long userId) {
        // Check ownership of the article
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        if (!article.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied: You do not own this article.");
        }

        // Check ownership of the cardboard
        Cardboard cardboard = cardboardRepository.findById(cardboardId)
                .orElseThrow(() -> new RuntimeException("Cardboard not found"));

        if (!cardboard.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied: You do not own this cardboard.");
        }

        // Assign and save
        article.setDefaultcardboard(cardboard);
        return articleRepository.save(article);
    }


    // For admin use (no user ownership check)
    public Article setDefaultCardboardGlobal(Long articleId, Long cardboardId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        Cardboard cardboard = cardboardRepository.findById(cardboardId)
                .orElseThrow(() -> new RuntimeException("Cardboard not found"));

        article.setDefaultcardboard(cardboard);
        return articleRepository.save(article);
    }


}
