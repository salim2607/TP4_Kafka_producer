package com.example.demo.services;

import java.util.Optional;

import com.example.demo.entity.Client;
import com.example.demo.entity.Commande;

public interface CommandeItf {
    
    Iterable<Commande> findAll();

    void create(String nom, Client client);
    
    Iterable<Commande> findAllByClient(Client client);

    Optional<Commande> findById(Long id);

    void addArticleToCommande(Long commandeId, String articleNom, int quantity, double prix);

    void removeArticleFromCommande(Long commandeId, Long articleId);

    public double calculTotalCommande(Commande commande);

    void valider(Long commandeId);
}