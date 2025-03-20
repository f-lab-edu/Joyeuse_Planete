package com.f_lab.joyeuse_planete.payment.service.thirdparty;

public abstract class PaymentProviderConstants {

  public static final int PAYMENT_API_ATTEMPTS = 3;
  public static final int PAYMENT_API_DELAYED_SECONDS = 3;
  public static final double PAYMENT_API_DELAYED_MULTIPLIER = 0.75;
}
