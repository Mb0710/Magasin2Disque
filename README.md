# Magasin2Disque

Schéma d'un achat de disque

1. Utilisateur clique "Acheter" sur disque.html
   ↓
2. Front appelle POST /api/commandes/acheter { buyerId, disqueId }
   ↓
3. MagasinDisque CommandeController reçoit la requête
   ↓
4. Valide et récupère le disque
   ↓
5. Appelle POST http://localhost:8081/api/transactions avec { buyerId, disqueId, amount, description }
   ↓
6. Transaction-service crée la Transaction en BD
   ↓
7. MagasinDisque met à jour disque (disponible = false)
   ↓
8. Retourne reçu avec tous les détails
   ↓
9. Front affiche succès et redirige