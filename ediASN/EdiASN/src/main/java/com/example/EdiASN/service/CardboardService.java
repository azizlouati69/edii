package com.example.EdiASN.service;

import com.example.EdiASN.dto.CardboardDTO;
import com.example.EdiASN.entity.Cardboard;
import com.example.EdiASN.repository.CardboardRepository;
import com.example.EdiASN.security.JwtService;
import com.example.EdiASN.security.JwtUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CardboardService {

    @Autowired
    private CardboardRepository cardboardRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtUtils jwtUtils;
    @PersistenceContext
    private EntityManager entityManager;
    public List<Cardboard> searchCardboards(CardboardDTO searchDTO, Long currentUserId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Cardboard> query = cb.createQuery(Cardboard.class);
        Root<Cardboard> root = query.from(Cardboard.class);

        List<Predicate> predicates = new ArrayList<>();

        if (searchDTO.getName() != null) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + searchDTO.getName().toLowerCase() + "%"));
        }
        if (searchDTO.getQuantity_pallet() != null) {
            predicates.add(cb.equal(root.get("quantity_pallet"), searchDTO.getQuantity_pallet()));
        }
        if (searchDTO.getRef_cardboard() != null) {
            predicates.add(cb.equal(root.get("ref_cardboard"), searchDTO.getRef_cardboard()));
        }
        if (searchDTO.getRef_pallet() != null) {
            predicates.add(cb.equal(root.get("ref_pallet"), searchDTO.getRef_pallet()));
        }
        if (searchDTO.getLength() != null) {
            predicates.add(cb.equal(root.get("length"), searchDTO.getLength()));
        }
        if (searchDTO.getWidth() != null) {
            predicates.add(cb.equal(root.get("width"), searchDTO.getWidth()));
        }
        if (searchDTO.getThickness() != null) {
            predicates.add(cb.equal(root.get("thickness"), searchDTO.getThickness()));
        }

        // Add the predicate to filter by current user's ID
        predicates.add(cb.equal(root.get("userId"), currentUserId));

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Cardboard> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    public List<Cardboard> searchCardboards(CardboardDTO searchDTO) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Cardboard> query = cb.createQuery(Cardboard.class);
        Root<Cardboard> root = query.from(Cardboard.class);

        List<Predicate> predicates = new ArrayList<>();

        if (searchDTO.getName() != null) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + searchDTO.getName().toLowerCase() + "%"));
        }
        if (searchDTO.getQuantity_pallet() != null) {
            predicates.add(cb.equal(root.get("quantity_pallet"), searchDTO.getQuantity_pallet()));
        }
        if (searchDTO.getRef_cardboard() != null) {
            predicates.add(cb.equal(root.get("ref_cardboard"), searchDTO.getRef_cardboard()));
        }
        if (searchDTO.getRef_pallet() != null) {
            predicates.add(cb.equal(root.get("ref_pallet"), searchDTO.getRef_pallet()));
        }
        if (searchDTO.getLength() != null) {
            predicates.add(cb.equal(root.get("length"), searchDTO.getLength()));
        }
        if (searchDTO.getWidth() != null) {
            predicates.add(cb.equal(root.get("width"), searchDTO.getWidth()));
        }
        if (searchDTO.getThickness() != null) {
            predicates.add(cb.equal(root.get("thickness"), searchDTO.getThickness()));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Cardboard> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }
    public Long getCurrentUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String token = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
            return jwtService.extractUserId(token);
        }
        throw new RuntimeException("Unauthenticated");
    }

    // === USER-SPECIFIC ===
    public List<Cardboard> getAllCardboardsByUser(Long userId) {
        return cardboardRepository.findByUserId(userId);
    }

    public Cardboard getCardboardById(Long id, Long userId) {
        return cardboardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Cardboard not found or not authorized"));
    }

    public Cardboard createCardboard(CardboardDTO dto, Long userId) {
        Cardboard cardboard = new Cardboard();
        cardboard.setName(dto.getName());
        cardboard.setQuantity_pallet(dto.getQuantity_pallet());
        cardboard.setRef_cardboard(dto.getRef_cardboard());
        cardboard.setRef_pallet(dto.getRef_pallet());
        cardboard.setLength(dto.getLength());
        cardboard.setWidth(dto.getWidth());
        cardboard.setThickness(dto.getThickness());
        cardboard.setUserId(userId);
        return cardboardRepository.save(cardboard);
    }

    public Cardboard updateCardboard(Long id, CardboardDTO dto, Long userId) {
        Cardboard cardboard = getCardboardById(id, userId);
        if (dto.getName() != null) cardboard.setName(dto.getName());
        if (dto.getQuantity_pallet() != null) cardboard.setQuantity_pallet(dto.getQuantity_pallet());
        if (dto.getRef_cardboard() != null) cardboard.setRef_cardboard(dto.getRef_cardboard());
        if (dto.getRef_pallet() != null) cardboard.setRef_pallet(dto.getRef_pallet());
        if (dto.getLength() != null)    cardboard.setLength(dto.getLength());
        if (dto.getWidth() != null)  cardboard.setWidth(dto.getWidth());
        if (dto.getThickness() != null)  cardboard.setThickness(dto.getThickness());
        return cardboardRepository.save(cardboard);
    }

    public void deleteCardboard(Long id, Long userId) {
        Cardboard cardboard = getCardboardById(id, userId);
        cardboardRepository.delete(cardboard);
    }

    // === GLOBAL ===

    public List<Cardboard> getAllCardboards() {
        return cardboardRepository.findAll();
    }

    public Cardboard getCardboardByIdGlobal(Long id) {
        return cardboardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cardboard not found"));
    }

    public Cardboard updateCardboardGlobal(Long id, CardboardDTO dto) {
        Cardboard cardboard = getCardboardByIdGlobal(id);

        if (dto.getName() != null) cardboard.setName(dto.getName());
        if (dto.getQuantity_pallet() != null) cardboard.setQuantity_pallet(dto.getQuantity_pallet());
        if (dto.getRef_cardboard() != null) cardboard.setRef_cardboard(dto.getRef_cardboard());
        if (dto.getRef_pallet() != null) cardboard.setRef_pallet(dto.getRef_pallet());
        if (dto.getLength() != null)    cardboard.setLength(dto.getLength());
        if (dto.getWidth() != null)  cardboard.setWidth(dto.getWidth());
        if (dto.getThickness() != null)  cardboard.setThickness(dto.getThickness());
        return cardboardRepository.save(cardboard);
    }

    public void deleteCardboardGlobal(Long id) {
        Cardboard cardboard = getCardboardByIdGlobal(id);
        cardboardRepository.delete(cardboard);
    }
}
