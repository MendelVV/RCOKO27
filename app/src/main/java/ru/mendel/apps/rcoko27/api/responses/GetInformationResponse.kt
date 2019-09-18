package ru.mendel.apps.rcoko27.api.responses

import ru.mendel.apps.rcoko27.data.InformationData

class GetInformationResponse : BaseResponse() {

    var information = arrayListOf<InformationData>()

}