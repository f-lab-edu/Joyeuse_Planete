package com.f_lab.joyeuse_planete.orders.repository;

import com.f_lab.joyeuse_planete.core.domain.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
}
