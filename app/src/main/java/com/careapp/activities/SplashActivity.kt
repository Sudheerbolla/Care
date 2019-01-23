package com.careapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import com.careapp.R
import com.careapp.utils.AppLocalStorage
import com.careapp.utils.Constants

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initComponents()
    }

    private fun initComponents() {
//        AppLocalStorage.getInstance(this).setBoolean(AppLocalStorage.PREF_IS_DATA_UPDATED, false)
        Handler().postDelayed({
            if (TextUtils.isEmpty(AppLocalStorage.getInstance(this).getString(AppLocalStorage.PREF_USER_TOKEN)))
                navigateToLoginActivity()
            else if (!AppLocalStorage.getInstance(this).getBoolean(AppLocalStorage.PREF_IS_DATA_UPDATED, false)) {
                navigateToDataUpdatingActivity()
            } else navigateToMainActivity()
        }, Constants.DELAY_TIME)
    }

    private fun navigateToDataUpdatingActivity() {
        startActivity(Intent(this, DataLoadingActivity::class.java))
        finishAffinity()
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    private fun navigateToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

}
