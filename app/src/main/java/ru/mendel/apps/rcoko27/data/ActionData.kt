package ru.mendel.apps.rcoko27.data

class ActionData(val actionName: String) {

    companion object {

        const val ACTION_TO_MAIN="ACTION_TO_MAIN"
        const val ACTION_TO_NEXT="ACTION_TO_NEXT"
        const val ACTION_ERROR="ACTION_ERROR"

        const val ITEM_TYPE = "ITEM_TYPE"
        const val ITEM_VERIFICATION = "ITEM_VERIFICATION"

    }

    val data: MutableMap<String, String> = mutableMapOf()
}