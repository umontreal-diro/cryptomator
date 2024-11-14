## Rapport de la tâche 3

Dans cette tâche, nous avons configuré la GitHub Action pour compiler et tester l'application Cryptomator avec cinq flags JVM différents. Chaque flag a été choisi pour examiner des aspects variés de la JVM, comme la gestion de la mémoire, l'optimisation et l'observabilité, pour évaluer leur impact potentiel sur la qualité, la performance et l'observabilité de l'application. Voici une description de chaque flag, ainsi que notre justification pour son choix.

***

### 1. -XX:+UseStringDeduplication

Description : Ce flag active la déduplication des chaînes de caractères dans le GC (Garbage Collection) G1, une technique qui permet d'identifier et d’éliminer les duplications de chaînes, réduisant ainsi la mémoire consommée.

#### Impact :

    Performance : Améliore l'utilisation de la mémoire en éliminant les copies redondantes de chaînes de caractères, surtout utile pour les applications avec de nombreux objets String identiques.
    Qualité : Aide à garantir que la gestion de la mémoire est efficace, ce qui peut indirectement améliorer la stabilité de l'application.

Justification : Étant donné que Cryptomator manipule des données sensibles, la réduction de la mémoire utilisée pour les chaînes de caractères peut optimiser la sécurité et les performances sans modifier le code source.

***

### 2. -XX:+PrintCompilation

Description : Ce flag active l’impression des événements de compilation JIT (Just-In-Time) dans les logs, ce qui donne des informations sur les méthodes compilées et leur état de compilation.

#### Impact :

    Observabilité : Fournit une vue détaillée des méthodes en cours de compilation, utile pour le débogage et l’analyse des performances.
    Qualité : Permet de diagnostiquer quels blocs de code sont optimisés par la JVM, facilitant l’identification des limitations de performance.

Justification : L'activation de PrintCompilation aide à comprendre comment Cryptomator est exécuté au niveau du code compilé, rendant les tests plus observables en cas de latence ou de problèmes de performances.

***

### 3. -XX:+HeapDumpOnOutOfMemoryError

Description : Lorsqu'une erreur de type OutOfMemoryError se produit, ce flag génère automatiquement un "heap dump" qui capture l’état de la mémoire JVM.

#### Impact :

    Observabilité : Facilite le diagnostic des fuites de mémoire en capturant l’état complet de la mémoire au moment de l’erreur.
    Qualité : Permet une résolution rapide et précise des problèmes de mémoire, contribuant à une meilleure fiabilité de l’application.

Justification : En cas de problème de mémoire pendant les tests, ce flag permet de récupérer facilement les informations nécessaires pour comprendre et corriger la source de l'erreur.

***

### 4. -XX:MaxInlineSize=20

Description : Ce flag spécifie la taille maximale des méthodes pouvant être en ligne. Ici, la taille est définie à 20 octets, ce qui limite les méthodes candidates pour l’inlining.

#### Impact :

    Performance : Contrôle la quantité d'inlining, qui est une technique d'optimisation consistant à remplacer un appel de méthode par son contenu pour gagner en performance.
    Qualité : Réduit la complexité du code compilé et aide à prévenir les effets indésirables dus à un inlining excessif.

Justification : Limiter l'inlining à une taille modérée permet de tester Cryptomator dans un environnement où les performances et l'optimisation sont mesurées pour des méthodes courtes, permettant ainsi de capturer des comportements plus précis lors des tests.

***

### 5. -XX:-DoEscapeAnalysis

Description : Ce flag désactive l’analyse d’échappement, une technique où la JVM évalue si les objets sont limités à un seul thread ou contexte et, dans le cas contraire, améliore l'allocation mémoire.

#### Impact :

    Performance : Désactiver l'analyse d’échappement peut réduire l'efficacité des optimisations mémoire, mais permet aussi de tester l'application dans des conditions moins optimisées.
    Qualité : Cette désactivation force un modèle plus conservateur d'allocation de mémoire, ce qui aide à identifier des problèmes potentiels qui pourraient être masqués par des optimisations trop agressives.

Justification : Cette configuration permet de tester Cryptomator sans les avantages de l’analyse d’échappement, révélant ainsi des problèmes de performance qui pourraient survenir dans des environnements avec des optimisations désactivées.

## Détails des modifications

Les changements à la GitHub Action sont assez simple, mais vraiment puissant.

Tout ces changments sont défini dans un seul commit: https://github.com/andrewkyle/cryptomator/commit/baae0d73c507675223c7f35f3593507f21c5f290

![Petit blague](./ci_joke.jpg)

### 1. Ajout de la Stratégie de Matrix pour les Flags JVM

La section `strategy.matrix` a été ajoutée à la job `test` pour permettre l'exécution des build et test avec différents flags. Chaque flag est appliqué individuellement à la JVM pour les tests. Cela est défini par la configuration suivante :

```
strategy:
  matrix:
    jvm_flags:
      - '-XX:+UseStringDeduplication'
      - '-XX:+PrintCompilation'
      - '-XX:+HeapDumpOnOutOfMemoryError'
      - '-XX:MaxInlineSize=20'
      - '-XX:-DoEscapeAnalysis'
```

### 2. Étapes de Compilation et de Test

L'étape principale de la job `test` a été modifiée pour intégrer les flags sélectionnés à l'aide de la variable d'environnement MAVEN_OPTS, qui est utilisée pour passer les flags JVM dans Maven.

Les modifications apportées à l'étape principale sont :

```
export MAVEN_OPTS="-XX:+PrintFlagsFinal ${{ matrix.jvm_flags }}"
echo "============================================================"
echo "Running the JVM with the following command line flags: $MAVEN_OPTS"
echo "============================================================"
```

#### Explications :

`export MAVEN_OPTS="-XX:+PrintFlagsFinal ${{ matrix.jvm_flags }}"` : Cette commande configure `MAVEN_OPTS` pour inclure le flag `-XX:+PrintFlagsFinal`, qui affiche tous les paramètres JVM au démarrage, et ajoute aussi un des flags passé par la matrice `jvm_flags`.

Logs d'exécution : Le message entre les lignes de `====` assurent une visibilité claire des flags actuellement utilisés dans chaque job parallèle, facilitant le suivi des builds.
