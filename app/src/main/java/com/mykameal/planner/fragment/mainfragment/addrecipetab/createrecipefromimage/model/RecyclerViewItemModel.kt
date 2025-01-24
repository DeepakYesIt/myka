package com.mykameal.planner.fragment.mainfragment.addrecipetab.createrecipefromimage.model

class RecyclerViewItemModel(uri: String, ingredientName: String, status: Boolean) {
    lateinit var uri:String
    lateinit var ingredientName:String
    var status:Boolean=false
}