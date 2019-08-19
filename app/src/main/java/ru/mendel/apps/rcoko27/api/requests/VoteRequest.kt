package ru.mendel.apps.rcoko27.api.requests

class VoteRequest(var voting : Int = 0,
                  var answer : Int = 0) : BaseRequest(action = ACTION_VOTE) {
    
}