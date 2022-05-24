package com.toploans.cashOcean.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceUtils(context: Context) {
    private val phoneNumberKey: String = "phone"
    private val policyKey: String = "policy"
    private val verificationCodeKey: String = "VerificationCode"
    private val passwordKey: String = "Password"
    private val registeredKey: String = "Registered"
    private val applicationCompleteKey: String = "ApplicationComplete"


    private val sharedPreferencesName: String = "CashOceanSharedPreference"
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()


    fun savePhoneNumber(phoneNumber: String) {
        editor.putString(phoneNumberKey, phoneNumber).commit()
    }

    fun getPhoneNumber(): String? {
        return sharedPreferences.getString(phoneNumberKey, "noPhoneNumber")
    }


    fun saveAcceptedPolicy(acceptedPolicy: Boolean) {
        editor.putBoolean(policyKey, acceptedPolicy).commit()
    }

    fun getAcceptedPolicy(): Boolean {
        return sharedPreferences.getBoolean(policyKey, false)
    }

    fun saveVerificationCode(verificationCode: String) {
        editor.putString(verificationCodeKey, verificationCode).commit()
    }

    fun getVerificationCode(): String? {
        return sharedPreferences.getString(verificationCodeKey, "noCode")
    }

    fun savePassword(password: String) {
        editor.putString(passwordKey, password).commit()
    }

    fun getPassword(): String? {
        return sharedPreferences.getString(passwordKey, "noPassword")
    }

    fun saveRegistered(registered: Boolean) {
        editor.putBoolean(registeredKey, registered).commit()
    }

    fun getRegistered(): Boolean {
        return sharedPreferences.getBoolean(registeredKey, false)
    }

    fun saveApplicationComplete(registered: Boolean) {
        editor.putBoolean(applicationCompleteKey, registered).commit()
    }

    fun getApplicationComplete(): Boolean {
        return sharedPreferences.getBoolean(applicationCompleteKey, false)
    }


}