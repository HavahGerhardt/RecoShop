# RecoShop
<p align="center">
<img src="https://github.com/HavahGerhardt/RecoShop/blob/master/app/src/main/res/drawable/reco_main_icon.png" width="250">
</p>
An android app that gathers products recommendations.

RecoShop is the opposite of the online marketplaces: a user searches for product recommendation, and then goes shopping.

## User Guide

[RecoShop user guide](https://github.com/HavahGerhardt/RecoShop/blob/master/User%20Guide.md)

## Database
I used [Firebase Database](https://firebase.google.com) to store categories, products, recommendations, and images.

I designed it like a tree: the roots are the categories, their children are the products, and the products' children are the recommendations. Each root/child has some fields, like name, his image reference (url) which have been stored in the Firebase Storage.

Firebase Database:
<p align="center">
<img src="https://github.com/HavahGerhardt/RecoShop/blob/master/screenshots/Firebase%20DB.png" width="600" >
</p>

Firebase Storage:
<p align="center">
<img src="https://github.com/HavahGerhardt/RecoShop/blob/master/screenshots/Firebase%20Storage.png" width="600" >
</p>
