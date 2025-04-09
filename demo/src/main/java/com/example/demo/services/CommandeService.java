package com.example.demo.services;

import java.util.Optional;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Article;
import com.example.demo.entity.Client;
import com.example.demo.entity.Commande;
import com.example.demo.entity.LigneCommande;
import com.example.demo.repositories.CommandeRepository;
import com.example.demo.repositories.ArticleRepository;
import com.example.demo.repositories.LigneCommandeRepository;

@Service
public class CommandeService implements CommandeItf {
    
    @Autowired
    private CommandeRepository repo;

    @Autowired
    private ArticleRepository articleRepo;

    @Autowired
    private LigneCommandeRepository ligneCommandeRepo;

    @Override
    public Iterable<Commande> findAll() {
        return repo.findAll();
    }

    @Override
    public void create(String nom, Client client) {
        var commande = new Commande(nom);
        commande.setClient(client);
        client.addCommande(commande);
        repo.save(commande);
    }

    @Override
    public Iterable<Commande> findAllByClient(Client client) {
        return repo.findAllByClient(client);
    }

    @Override
    public void addArticleToCommande(Long commandeId, String articleNom, int quantity, double prix) {
        Optional<Commande> optionalCommande = repo.findById(commandeId);

        if (optionalCommande.isPresent()) {
            Commande commande = optionalCommande.get();
            Optional<Article> optionalArticle = articleRepo.findByNomArticleAndPrixUnitaire(articleNom, prix);
            Article article;
            if (optionalArticle.isPresent()) {
                article = optionalArticle.get();
            } else {
                article = new Article(articleNom, prix);
                articleRepo.save(article);
            }

            Optional<LigneCommande> existingLigneCommande = commande.getLigneCommande().stream()
                .filter(lc -> lc.getArticle().getNomArticle().equals(articleNom) && lc.getArticle().getPrixUnitaire() == prix)
                .findFirst();

            if (existingLigneCommande.isPresent()) {
                LigneCommande ligneCommande = existingLigneCommande.get();
                ligneCommande.setNbArticle(ligneCommande.getNbArticle() + quantity);
                ligneCommandeRepo.save(ligneCommande);
            } else {
                LigneCommande ligneCommande = new LigneCommande();
                ligneCommande.setArticle(article);
                ligneCommande.setNbArticle(quantity);
                ligneCommande.setCommande(commande);
                ligneCommandeRepo.save(ligneCommande);
                commande.getLigneCommande().add(ligneCommande);
            }

            repo.save(commande);
        } else {
            throw new NoSuchElementException("Commande not found");
        }
    }

    @Override
    public void removeArticleFromCommande(Long commandeId, Long articleId) {
        Commande commande = repo.findById(commandeId).orElseThrow();
        commande.getLigneCommande().removeIf(lc -> lc.getArticle().getId().equals(articleId));
        repo.save(commande);
    }

    @Override
    public Optional<Commande> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public double calculTotalCommande(Commande commande) {
        double somme = 0;
        for (LigneCommande ligneCommande : commande.getLigneCommande()) {
            somme += ligneCommande.getNbArticle() * ligneCommande.getArticle().getPrixUnitaire();
        }
        return somme;
    }

    @Override
    public void valider(Long commandeId) {
        Commande commande = repo.findById(commandeId).orElseThrow();
        commande.setEtat("Validée");
        repo.save(commande);
    }
}
