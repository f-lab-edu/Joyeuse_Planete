package com.f_lab.joyeuse_planete.payment.service.thirdparty.token;


import lombok.Getter;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;
import static lombok.AccessLevel.PUBLIC;

@Getter(PUBLIC)
@Setter(PROTECTED)
public abstract class PaymentToken {

  protected String processor;
}
