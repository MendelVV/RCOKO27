package ru.mendel.apps.rcoko27.api.requests

open class BaseRequest(var action: String? =null) {

    companion object{
        const val ACTION_AUTH = "auth"
        const val ACTION_REG = "reg"
        const val ACTION_VERIFY_CODE = "verify_code"
        const val ACTION_REGISTRATION = "registration"

        const val ACTION_RESET_PASSWORD = "reset_password"
        const val ACTION_NEW_PASSWORD = "new_password"

        const val ACTION_GET_EVENTS = "get_events"
        const val ACTION_REFRESH_EVENTS = "refresh_events"
        const val ACTION_UPDATE_EVENTS = "update_events"

        const val ACTION_GET_EVENT = "get_event"

        const val ACTION_SEND_MESSAGE = "send_message"
        const val ACTION_REMOVE_MESSAGE = "remove_message"
        const val ACTION_EDIT_MESSAGE = "edit_message"
        const val ACTION_UPDATE_MESSAGES = "update_messages"

        const val ACTION_VOTE = "vote"

        const val ACTION_GET_ACTIVITIES = "get_activities"
        const val ACTION_GET_SETTINGS = "get_settings"
        const val ACTION_EDIT_SETTINGS = "edit_settings"
    }

    var appname : String? = null

}