package com.yesitlabs.mykaapp

interface OnItemClickedListener {

    fun itemClicked(position: Int?, list: MutableList<String>, status:String?, type:String?)
}