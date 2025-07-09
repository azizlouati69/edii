package com.example.EdiASN.service;

import com.example.EdiASN.dto.ArticleClientRequest;
import com.example.EdiASN.entity.Article;
import com.example.EdiASN.entity.Client;
import com.example.EdiASN.entity.ArticleClient;
import com.example.EdiASN.repository.ArticleRepository;
import com.example.EdiASN.repository.ClientRepository;
import com.example.EdiASN.repository.ArticleClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleClientService {

    private final ArticleRepository articleRepository;
    private final ClientRepository clientRepository;
    private final ArticleClientRepository articleClientRepository;

    public ArticleClientService(ArticleRepository articleRepository, ClientRepository clientRepository, ArticleClientRepository articleClientRepository) {
        this.articleRepository = articleRepository;
        this.clientRepository = clientRepository;
        this.articleClientRepository = articleClientRepository;
    }

    public ArticleClient createArticleClientg(Long articleId, Long clientId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        ArticleClient articleClient = new ArticleClient();
        articleClient.setArticle(article);
        articleClient.setClient(client);

        articleClient.setUserId(userId);
        return articleClientRepository.save(articleClient);
    }
    public ArticleClient createArticleClient(Long articleId, Long clientId, Long currentUserId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Check ownership
        if (!article.getUserId().equals(currentUserId)) {
            throw new RuntimeException("You do not own this article");
        }
        if (!client.getUserId().equals(currentUserId)) {
            throw new RuntimeException("You do not own this client");
        }

        ArticleClient articleClient = new ArticleClient();
        articleClient.setArticle(article);
        articleClient.setClient(client);
        articleClient.setUserId(currentUserId);
        return articleClientRepository.save(articleClient);
    }

    public List<ArticleClient> getAll() {
        return articleClientRepository.findAll();
    }

    public List<ArticleClient> getAllByUserId(Long userId) {
        return articleClientRepository.findByUserId(userId);
    }

    public ArticleClient getById(Long id) {
        return articleClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }
    public ArticleClient getByIdForUser(Long id, Long userId) {
        return articleClientRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Client not found or not authorized"));
    }


    public ArticleClient update(Long id, ArticleClientRequest request) {
        ArticleClient ac = getById(id);

        if (request.getArticleId() != null) {
            Article article = articleRepository.findById(request.getArticleId())
                    .orElseThrow(() -> new RuntimeException("Article not found"));
            ac.setArticle(article);
        }

        if (request.getClientId() != null) {
            Client client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            ac.setClient(client);
        }



        return articleClientRepository.save(ac);
    }
    public ArticleClient getClientById(Long id, Long userId) {
        return articleClientRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Client not found or not authorized"));
    }
    public ArticleClient updateForUser(Long id, ArticleClientRequest request, Long userId) {
        ArticleClient ac = getClientById(id, userId);
        if (request.getArticleId() != null) {
            Article article = articleRepository.findById(request.getArticleId())
                    .orElseThrow(() -> new RuntimeException("Article not found"));
            ac.setArticle(article);
        }

        if (request.getClientId() != null) {
            Client client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            ac.setClient(client);
        }



        return articleClientRepository.save(ac);
    }

    public void delete(Long id) {
        ArticleClient ac = getById(id);
        articleClientRepository.delete(ac);
    }

    public void deleteForUser(Long id, Long userId) {
        ArticleClient ArticleClient = getClientById(id, userId);
        articleClientRepository.delete(ArticleClient);
    }

}
