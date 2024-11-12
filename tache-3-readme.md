# Tâche 3 - Modification de la GitHub Action pour Multiple Configurations JVM


## Objectifs de la Tâche
La tâche 3 a deux objectifs principaux :
1. Modifier une GitHub Action pour exécuter plusieurs builds avec différentes configurations de la JVM.
2. Explorer les options (`flags`) disponibles de la JVM pour évaluer leurs effets sur le build et les tests.

## Modifications Apportées à `test.yml`
Pour cette tâche, nous avons modifié le fichier `test.yml` afin d'exécuter les tests avec plusieurs configurations de la JVM en utilisant une matrice de flags JVM. Voici les changements principaux :
- Nous avons ajouté une matrice `jvm_flags` dans `test.yml` pour exécuter chaque build avec un flag différent.
- Les builds sont désormais exécutés avec les options suivantes :
  - `-XX:+UseParallelGC`
  - `-XX:+UseG1GC`
  - `-XX:+UnlockDiagnosticVMOptions -XX:+ExtendedDTraceProbes`

Chaque configuration est appliquée en utilisant la variable `MAVEN_OPTS` pour ajuster les options de la JVM lors de l'exécution de `mvn verify`.

## Liste des Flags JVM Utilisés
Les flags suivants ont été sélectionnés pour leurs potentiels effets sur la performance et la gestion mémoire :
- **`-XX:+UseParallelGC`** : Utilise le ramasse-miettes parallèle pour améliorer les performances en multi-threading.
- **`-XX:+UseG1GC`** : Utilise le ramasse-miettes G1, optimisé pour de faibles pauses de traitement.
- **`-XX:+UnlockDiagnosticVMOptions -XX:+ExtendedDTraceProbes`** : Déverrouille les options de diagnostic et active les sondes DTrace pour obtenir des informations plus détaillées.

## Résultats
Les builds ont été exécutés avec succès pour chaque configuration. Les résultats des tests et des couvertures de code ont été enregistrés dans les logs GitHub Actions.



## Conclusion
Ce workflow avec une matrice permet d'explorer rapidement plusieurs configurations de JVM et d'observer leur impact sur le projet. Cette approche est utile pour identifier les configurations optimales pour la performance ou le diagnostic de problèmes.

---

**Note** : Les fichiers YAML modifiés pour cette tâche se trouvent dans le dossier `.github/workflows`.
