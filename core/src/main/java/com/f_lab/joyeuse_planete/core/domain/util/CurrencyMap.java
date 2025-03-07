package com.f_lab.joyeuse_planete.core.domain.util;

import java.math.RoundingMode;
import java.util.Map;

public abstract class CurrencyMap {
  public static final Map<String, Integer> scales = Map.of(
      "USD", 2,
      "GBP", 2,
      "KRW", 0
  );

  public static final Map<String, RoundingMode> rounding = Map.of(
      "USD", RoundingMode.FLOOR,
      "GBP", RoundingMode.FLOOR,
      "KRW", RoundingMode.FLOOR
  );
}
