package com.mykameal.planner

interface OnItemClickedListener {

    fun itemClicked(position: Int?, list: MutableList<String>, status:String?, type:String?)
}