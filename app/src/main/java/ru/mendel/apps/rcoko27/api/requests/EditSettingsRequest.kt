package ru.mendel.apps.rcoko27.api.requests

class EditSettingsRequest(var name : String? = null,
                          var role : Int = 0) : BaseRequest(action = ACTION_EDIT_SETTINGS) {
    
}