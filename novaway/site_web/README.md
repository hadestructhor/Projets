J'ai utilisé le site [suivant](https://www.mockaroo.com/) pour générer les fichiers json pour les recettes (le fichier recipes.json dans data) et utilisateur (le fichier users.jsondans le dossier data également).

Je n'ai modifié que la première ligne pour le fichier des recettes pour inclure celle qu'il y avait en exemple.



Pour les utilisateurs je n'en ai mis que 5 avec des images de profils. Si vous avez un écran assez large vous pourrez voir l'image par défauts qu'on ceux qui n'ont en pas.

Vous pouvez changer les attributs dans les deux fichiers json. 

Si vous voulez modifier par exemple l'image du tout premier utilisateur, vous pouvez modifier **profil_pic_link** dans users pour une image de votre choix du moment qu'elle se trouve dans la racine du site et que vous mettez le bon chemin dans **profil_pic_link**.



Les logiciels que j'ai utilisés sont :

- **Sumblime Text**: pour écrire et modifier les fichiers html, javascript et css.
- **Lunacy**: pour reproduire les icones en .svg.
- **Laragon**: pour simuler un serveur pour les requêtes http afin de récupérer les donnés dans les fichiers json.



Je n'ai pas eu le temps pour faire afficher le nom et prénom des utilisateurs mais vous verrez dans le fichier javascript l'endroit où j'ajoute des balises \<div> avec une classe *hidden* pour essayer de les afficher ensuite.