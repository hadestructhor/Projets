Ce site web était un test pour une candidature de développeur web front end que j'ai réalisé en deux jours.



J'ai utilisé le site [suivant](https://www.mockaroo.com/) pour générer les fichiers json pour les recettes (le fichier recipes.json dans data) et utilisateur (le fichier users.json dans le dossier data également).

Je n'ai modifié que la première ligne pour le fichier des recettes pour inclure celle qu'il y avait en exemple.

Pour les utilisateurs je n'en ai mis que 5 avec des images de profils. Si vous avez un écran assez large vous pourrez voir l'image par défauts qu'on ceux qui n'ont en pas.

Vous pouvez changer les attributs dans les deux fichiers json. 

Si vous voulez modifier par exemple l'image du tout premier utilisateur, vous pouvez modifier **profil_pic_link** dans users pour une image de votre choix du moment qu'elle se trouve dans la racine du site et que vous mettez le bon chemin dans **profil_pic_link**.



Les logiciels que j'ai utilisés sont :

- **Sumblime Text**: pour écrire et modifier les fichiers html, javascript et css.
- **Lunacy**: pour reproduire les icones en .svg.
- **Laragon**: pour simuler un serveur pour les requêtes http afin de récupérer les donnés dans les fichiers json. J'ai placé le dossier site_web dans ROOT sur Laragon avant de lancer un serveur en local avec START ALL.
