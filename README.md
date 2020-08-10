# Graphiques générés pour l'ENS de Lyon en stage

Vous pouvez voir [ici](graphiques_generes/explication_des_graphiques.md) deux graphiques que j'ai eu à générer pour l'ENS en 5 semaines pour la validation de mon DUT informatique.

Vous trouverez également deux scripts python [ici](/scripts_python) qui ont chacun permis de générer un des deux graphiques. J'ai également mis à disposition la documentation que j'ai générés pour tous les scripts que j'ai écris [ici](/scripts_python/Documentation.md) si vous voulez y jeter un œil.

J'ai eu à générer bien plus de graphiques et vous n'avez évidemment pas accès aux données utilisées pour générer ces graphiques.

# Site web test pour une entreprise

Vous trouverez dans [ce dossier](/site_web_test/site_web/readme.md) un site web responsive sur des recettes de cuisine que j'ai réalisé suite à une candidature.

# Applications mobile codé en cours

Vous avez ci-dessous une explication des deux applications Android que j'ai codé en cours de développement mobile.

## Ajouter une application à votre téléphone

Vous aurez dans le dossier [APK](/APK) les APKs des deux applications.

Connecter votre téléphone à votre ordinateur en ayant activé le débogage USB et en ajoutant l'APK de l'application que vous voulez tester dans un dossier.

Une fois que l'APK est mis sur votre téléphone, vous n'avez plus qu'à aller dans le dossier où vous avez mis l'APK et à le lancer.

## Application Vélov

Cette application liste tous les stations Vélov d'une ville. La ville par défaut est Lyon.

### Permissions que l'application demande et leur utilité

Vous pouvez utiliser l'application sans accepter aucune des deux permissions suivantes qu'elle demande :

- Stockage : permet d'enregistrer les stations que vous ajouterez en favoris
- Localisation : permettait d'afficher votre emplacement sur la carte par rapport aux stations mais ne sert plus à rien

### Ajouter/retirer une station en favoris

Vous avez juste à cliquer sur l'étoile peu importe dans quel fenêtre vous vous trouvez.

### Changer de ville

Vous devez être soit sur la carte soit sur la page qui s'affiche après l'affichage du logo.

Ensuite vous glissez de la gauche de votre écran à la droite. 

Une fenêtre de préférence s'affiche et vous pouvez simplement changer la ville.

Chaque favoris est sauvegardé pour une ville en particulier.



## Application de contact

C'est une application qui permet d'ajouter des contacts tout simplement. 

### Ajouter un contact

Vous devez appuyer sur le signe + en bas de la page pour ajouter un contact.

Vous devez remplir les trois champs suivants:

- Nom : Vous pouvez mettre ce que vous voulez, un nombre, un chiffre ou une chaîne de caractères.
- Prénom : Même chose que pour Nom
- Numéro : Même chose que pour les deux champs au dessus

Je n'avais pas à ajouter de regex ou quoique ce soit pour cette application donc c'est pour cette raison que vous n'avez pas besoin d'entrer un numéro valide car je n'avais pas le temps ni l'envie de faire les regex pour tous les pays.

Vous avez également pas besoin de remplir les autres champs ni à choisir Homme ou Femme comme sexe, ce qui affichera un avatar basique pour le contact en question.

### Afficher les infos d'un contact

Pour afficher les infos d'un contact vous avez juste à cliquer dessus.

### Supprimer un contact

Restez appuyé longtemps sur un contact le supprimera

# Applications mobile codé hors cours

Vous avez ci-dessous une des application que je code actuellement tout en apprenant le Kotlin.

### beatboxloopstation

Pour l'instant il n'y a qu'un RecyclerView qui contient des pistes qui serviront à enregistrer et jouer des sons en boucle.

Le but de cette application est de m'entraîner et de reproduire la loopstation de Boss (la RC 505 si vous voulez voir à quoi elle ressemble) car je n'ai trouvé aucune application sur le Google Play Store qui permet de faire ce que je souhaite faire avec.

