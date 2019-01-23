package com.careapp.wsutils

object WSUtils {

    val TAG = "CARE"

    val CONNECTION_TIMEOUT: Long = 125

    val DEV_BASE_URL = "http://ec2-13-233-65-185.ap-south-1.compute.amazonaws.com:3000"
    val QA_BASE_URL = "http://ec2-13-233-65-185.ap-south-1.compute.amazonaws.com:3000"
    val LIVE_BASE_URL = "http://ec2-13-233-65-185.ap-south-1.compute.amazonaws.com:3000"

    val BASE_URL = DEV_BASE_URL

    val REQ_FOR_CREATE_CLIENT = 100
    val REQ_FOR_LOGIN = 101
    val REQ_FOR_GET_ANGANWADI_CENTERS = 102
    val REQ_FOR_GET_QUESTIONS = 103
    val REQ_FOR_GET_CATEGORIES = 104
    val REQ_FOR_GET_QUESTIONS_FOR_CATEGORY = 105
    val REQ_FOR_ANSWER_QUESTION = 106
    val REQ_FOR_GET_HOMES = 107
    val REQ_FOR_GET_DISTRICTS = 108
    val REQ_FOR_GET_BLOCKS = 109
    val REQ_FOR_GET_AWCS = 110
    val REQ_FOR_ADD_AWCS = 111
    val REQ_FOR_LOGOUT = 112

    val NAME = "name"
    val CID = "cid"
    val ID = "id"
    val USER_ID = "userId"
    val TOKEN = "token"
    val ACCOUNT_STATUS = "accountStatus"
    val ACCOUNT_TYPE = "type"
    val BARCODE = "barcode"
    val USER_TYPE = "userType"
    val ORDER_BACKEND_STATUS = "orderBackendStatus"

}
