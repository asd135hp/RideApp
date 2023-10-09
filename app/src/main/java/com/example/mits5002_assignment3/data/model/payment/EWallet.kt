package com.example.mits5002_assignment3.data.model.payment

import com.example.mits5002_assignment3.data.model.common.PaymentType

/**
 * An object representing various types of e-wallet app payment (PayPal, ApplePay and GooglePay)
 */
class EWallet (
    paymentInt: Int,
    rideId: Int,
    amount: Double,
    type: PaymentType
) : Payment(paymentInt, rideId, amount, type) {
    fun payPalPayment(){

    }

    fun applePayPayment(){

    }

    fun googlePayPayment(){

    }
}