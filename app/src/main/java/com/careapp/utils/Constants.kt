package com.careapp.utils

class Constants {

    companion object {
        val ONE_DAY_INTERVAL = 24 * 60 * 60 * 1000L // 1 Day

        //        val DELAY_TIME = 2000.toLong()
        val QUARTER_SECOND_DELAY = 250.toLong()
        val HALF_SECOND_DELAY = 500.toLong()
        val ONE_SECOND_DELAY = 1000.toLong()
        val BACK_PRESSED_TIME = 2000.toLong()

        val DELAY_TIME = QUARTER_SECOND_DELAY
        var bTwo: Boolean? = false

        val LOCATION_REQUEST_CODE = 1

        val TWENTY_FIVE = 25
        val THIRTY = 30
        val FORTY = 40
        val FIFTY = 50

        val CREATE_ANGANWADI = 1
        val REGISTRATION_REQUEST = 2
        val CHECKOUT_REQUEST = 3
        val CREATE_FAMILY_MEMBER = 4
        val LOCATIONS_SELECTION = 5
        val EDIT_PROFILE = 6

        val PERMISSION_LOCATION_SERVICES = 1001
        val CONTENT_TYPE = "Content-Type"
        val CONTENT_TYPE_JSON = "application/json"
        val CONTENT_TYPE_TEXT_PLAIN = "text/plain"
        val CONTENT_TYPE_FORM_URL_ENCODED = "application/x-www-form-urlencoded"
        val AUTHORIZATION = "Authorization"
        val BEARER = "Bearer "
        val EMAIL_ID = "email"
        val PASSWORD = "password"
        val ACTIVE = "active"

        val LOCATION_ID = "location_id"
        val CUSTOMER_ID = "customer_id"

        val SITE_URL = "https://5thvital.com/"

        const val BROADCAST_DATA_SYNC = "com.careapp.BROADCAST_DATA_SYNC"
        const val DATA_STATUS = "com.careapp.STATUS"


    }

}