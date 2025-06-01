package com.example.EdiASN.service;


import com.example.EdiASN.dto.ClientDTO;
import com.example.EdiASN.entity.Client;
import com.example.EdiASN.repository.ClientRepository;
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
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtUtils jwtUtils;
    @PersistenceContext
    private EntityManager entityManager;
    public List<Client> searchClients(ClientDTO searchDTO, Long currentUserId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> query = cb.createQuery(Client.class);
        Root<Client> root = query.from(Client.class);

        List<Predicate> predicates = new ArrayList<>();

        if (searchDTO.getName() != null) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + searchDTO.getName().toLowerCase() + "%"));
        }
        if (searchDTO.getLoc11() != null) {
            predicates.add(cb.equal(root.get("loc11"), searchDTO.getLoc11()));
        }
        if (searchDTO.getRef_client() != null) {
            predicates.add(cb.equal(root.get("ref_client"), searchDTO.getRef_client()));
        }
        if (searchDTO.getAdress() != null) {
            predicates.add(cb.equal(root.get("adress"), searchDTO.getAdress()));
        }
        if (searchDTO.getNad() != null) {
            predicates.add(cb.equal(root.get("nad"), searchDTO.getNad()));
        }
        if (searchDTO.getPia() != null) {
            predicates.add(cb.equal(root.get("pia"), searchDTO.getPia()));
        }
        if (searchDTO.getEdi_adress() != null) {
            predicates.add(cb.equal(root.get("edi_adress"), searchDTO.getEdi_adress()));
        }
        if (searchDTO.getLoc159() != null) {
            predicates.add(cb.equal(root.get("loc159"), searchDTO.getLoc159()));
        }
        if (searchDTO.getSiret() != null) {
            predicates.add(cb.equal(root.get("siret"), searchDTO.getSiret()));
        }

        // Add the predicate to filter by current user's ID
        predicates.add(cb.equal(root.get("userId"), currentUserId));

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Client> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    public List<Client> searchClients(ClientDTO searchDTO) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> query = cb.createQuery(Client.class);
        Root<Client> root = query.from(Client.class);

        List<Predicate> predicates = new ArrayList<>();

        if (searchDTO.getName() != null) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + searchDTO.getName().toLowerCase() + "%"));
        }
        if (searchDTO.getLoc11() != null) {
            predicates.add(cb.equal(root.get("loc11"), searchDTO.getLoc11()));
        }
        if (searchDTO.getRef_client() != null) {
            predicates.add(cb.equal(root.get("ref_client"), searchDTO.getRef_client()));
        }
        if (searchDTO.getAdress() != null) {
            predicates.add(cb.equal(root.get("adress"), searchDTO.getAdress()));
        }
        if (searchDTO.getNad() != null) {
            predicates.add(cb.equal(root.get("nad"), searchDTO.getNad()));
        }
        if (searchDTO.getPia() != null) {
            predicates.add(cb.equal(root.get("pia"), searchDTO.getPia()));
        }
        if (searchDTO.getEdi_adress() != null) {
            predicates.add(cb.equal(root.get("edi_adress"), searchDTO.getEdi_adress()));
        }
        if (searchDTO.getLoc159() != null) {
            predicates.add(cb.equal(root.get("loc159"), searchDTO.getLoc159()));
        }
        if (searchDTO.getSiret() != null) {
            predicates.add(cb.equal(root.get("siret"), searchDTO.getSiret()));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Client> typedQuery = entityManager.createQuery(query);
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
    public List<Client> getAllClientsByUser(Long userId) {
        return clientRepository.findByUserId(userId);
    }

    public Client getClientById(Long id, Long userId) {
        return clientRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Client not found or not authorized"));
    }

    public Client createClient(ClientDTO dto, Long userId) {
        Client client = new Client();
        client.setName(dto.getName());
        client.setNad(dto.getNad());
        client.setRef_client(dto.getRef_client());
        client.setAdress(dto.getAdress());
        client.setEdi_adress(dto.getEdi_adress());
        client.setLoc11(dto.getLoc11());
        client.setLoc159(dto.getLoc159());
        client.setSiret(dto.getSiret());
        client.setPia(dto.getPia());
        client.setUserId(userId);
        return clientRepository.save(client);
    }

    public Client updateClient(Long id, ClientDTO dto, Long userId) {
        Client client = getClientById(id, userId);
        if (dto.getName() != null) client.setName(dto.getName());
        if (dto.getNad() != null) client.setNad(dto.getNad());
        if (dto.getRef_client() != null) client.setRef_client(dto.getRef_client());
        if (dto.getAdress() != null) client.setAdress(dto.getAdress());
        if (dto.getEdi_adress() != null)    client.setEdi_adress(dto.getEdi_adress());
        if (dto.getLoc11() != null)  client.setLoc11(dto.getLoc11());
        if (dto.getLoc159() != null)  client.setLoc159(dto.getLoc159());
        if (dto.getSiret() != null)  client.setSiret(dto.getSiret());
        if (dto.getPia() != null)  client.setPia(dto.getPia());

        return clientRepository.save(client);
    }

    public void deleteClient(Long id, Long userId) {
        Client client = getClientById(id, userId);
        clientRepository.delete(client);
    }

    // === GLOBAL ===

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client getClientByIdGlobal(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    public Client updateClientGlobal(Long id, ClientDTO dto) {
        Client client = getClientByIdGlobal(id);

        if (dto.getName() != null) client.setName(dto.getName());
        if (dto.getNad() != null) client.setNad(dto.getNad());
        if (dto.getRef_client() != null) client.setRef_client(dto.getRef_client());
        if (dto.getAdress() != null) client.setAdress(dto.getAdress());
        if (dto.getEdi_adress() != null)    client.setEdi_adress(dto.getEdi_adress());
        if (dto.getLoc11() != null)  client.setLoc11(dto.getLoc11());
        if (dto.getLoc159() != null)  client.setLoc159(dto.getLoc159());
        if (dto.getSiret() != null)  client.setSiret(dto.getSiret());
        if (dto.getPia() != null)  client.setPia(dto.getPia());
        return clientRepository.save(client);
    }

    public void deleteClientGlobal(Long id) {
        Client client = getClientByIdGlobal(id);
        clientRepository.delete(client);
    }
}
