package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.entity.Article;

public interface ArticleRepository extends CrudRepository<Article, Long> {

    Optional<Article> findByNomArticleAndPrixUnitaire(String articleNom, double prix);
}