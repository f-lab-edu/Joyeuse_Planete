package com.f_lab.joyeuse_planete.orders.service;

import com.f_lab.joyeuse_planete.core.domain.Voucher;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.orders.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoucherService {

  private final VoucherRepository voucherRepository;

  public Voucher getValidVoucher(Long voucherId) {
    if (voucherId == null)
      return null;

    Voucher voucher = findVoucherById(voucherId);
    validateVoucher(voucher);
    return voucher;
  }

  private Voucher findVoucherById(Long voucherId) {
    return voucherRepository.findById(voucherId)
        .orElseThrow(() -> new JoyeusePlaneteApplicationException(ErrorCode.VOUCHER_NOT_EXIST_EXCEPTION));
  }

  private void validateVoucher(Voucher voucher) {
    if (voucher.hasExpired()) {
      throw new JoyeusePlaneteApplicationException(ErrorCode.VOUCHER_EXPIRED_EXCEPTION);
    }
  }
}
