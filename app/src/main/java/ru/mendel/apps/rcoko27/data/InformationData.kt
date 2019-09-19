package ru.mendel.apps.rcoko27.data

class InformationData {

    var code : Int = 0
    var event : Int = 0
    var date: String? = null
    var text: String? = null
    var gmt: String? = null


    override fun equals(other: Any?): Boolean {
        if (other is InformationData){
            return code == other.code
        }
        return false
    }
}