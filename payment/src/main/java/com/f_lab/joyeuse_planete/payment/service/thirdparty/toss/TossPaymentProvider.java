package com.f_lab.joyeuse_planete.payment.service.thirdparty.toss;

import com.f_lab.joyeuse_planete.core.domain.Payment;

import com.f_lab.joyeuse_planete.payment.service.thirdparty.PaymentProvider;
import com.f_lab.joyeuse_planete.payment.service.thirdparty.exceptions.PaymentAPIRetryableException;
import com.f_lab.joyeuse_planete.payment.service.thirdparty.response.PaymentResponse;
import com.f_lab.joyeuse_planete.payment.service.thirdparty.token.PaymentToken;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

import static com.f_lab.joyeuse_planete.payment.service.thirdparty.PaymentProviderConstants.*;
import static com.f_lab.joyeuse_planete.payment.service.thirdparty.toss.TossExceptionTranslator.RETRYABLE;

public class TossPaymentProvider implements PaymentProvider {

  private static final String PROCESSOR = "토스";
  private static final String TOSS_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
  private static final String TOSS_CANCEL_URL = "https://api.tosspayments.com/v1/payments";

  @Value("${toss.secret-key}")
  private String TOSS_SECRET_KEY;

  @Override
  public boolean supports(String processor) {
    return PROCESSOR.equalsIgnoreCase(processor);
  }

  @Override
  public Mono<PaymentResponse> confirmPayment(PaymentToken token) {
    TossPaymentToken tossToken = (TossPaymentToken) token;

    WebClient client = WebClient.create(TOSS_CONFIRM_URL);

    Mono<TossPaymentResponse> result = client.post()
        .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((TOSS_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8)))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(TossConfirmRequestBody.from(tossToken))
        .retrieve()

        .onStatus(HttpStatusCode::is4xxClientError, res -> res.bodyToMono(TossPaymentResponseError.class)
                .flatMap(err -> Mono.error(TossExceptionTranslator.translate(err)))
        )

        .onStatus(HttpStatusCode::is5xxServerError, res -> res.bodyToMono(TossPaymentResponseError.class)
            .flatMap(err -> Mono.error(RETRYABLE))
        )

        .bodyToMono(TossPaymentResponse.class)

        .retryWhen(Retry.backoff(PAYMENT_API_ATTEMPTS, Duration.ofSeconds(PAYMENT_API_DELAYED_SECONDS))
            .jitter(PAYMENT_API_DELAYED_MULTIPLIER)
            .filter(ex -> ex instanceof PaymentAPIRetryableException)
            .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> RETRYABLE)));

    return result.flatMap(TossPaymentResponse::from);
  }

  @Override
  public Mono<PaymentResponse> cancelPayment(PaymentToken token) {
    TossPaymentToken tossToken = (TossPaymentToken) token;

    WebClient client = WebClient.create(TOSS_CANCEL_URL);

    Mono<TossPaymentResponse> result = client.post()
        .uri("/{paymentKey}/cancel", tossToken.getPaymentKey())
        .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((TOSS_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8)))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(TossCancelRequestBody.from())
        .retrieve()

        .onStatus(HttpStatusCode::is4xxClientError, res -> res.bodyToMono(TossPaymentResponseError.class)
            .flatMap(err -> Mono.error(TossExceptionTranslator.translate(err)))
        )

        .onStatus(HttpStatusCode::is5xxServerError, res -> Mono.error(new PaymentAPIRetryableException()))

        .bodyToMono(TossPaymentResponse.class)

        .retryWhen(Retry.backoff(PAYMENT_API_ATTEMPTS, Duration.ofSeconds(PAYMENT_API_DELAYED_SECONDS))
            .jitter(PAYMENT_API_DELAYED_MULTIPLIER)
            .filter(ex -> ex instanceof PaymentAPIRetryableException)
            .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> RETRYABLE)));

    return result.flatMap(TossPaymentResponse::from);
  }

  @Override
  public TossPaymentToken generateToken(Payment payment) {
    return TossPaymentToken.createToken(
        payment.getProcessor(),
        payment.getPaymentKey(),
        String.valueOf(payment.getOrder().getId()),
        String.valueOf(payment.getOrder().getTotalCost()));
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class TossPaymentResponse {

    private String mId;
    private String version;
    private String lastTransactionKey;
    private String paymentKey;
    private String orderId;
    private String orderName;
    private String currency;
    private String method;
    private String status;
    private String type;
    private String easyPay;
    private String country;
    private Integer totalAmount;
    private Integer balanceAmount;
    private Integer suppliedAmount;
    private Integer vat;
    private Integer taxFreeAmount;
    private Integer taxExemptionAmount;

    private List<CancelInfo> cancels;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class CancelInfo {
      private String cancelReason;
      private String canceledAt;
      private Integer cancelAmount;
      private Integer taxFreeAmount;
      private Integer taxExemptionAmount;
      private Integer refundableAmount;
      private Integer transferDiscountAmount;
      private Integer easyPayDiscountAmount;
      private String transactionKey;
      private String receiptKey;
      private String cancelStatus;
      private String cancelRequestId;
    }

    public static Mono<PaymentResponse> from(TossPaymentResponse res) {
      return Mono.just(
          PaymentResponse.builder()
              .paymentKey(res.getPaymentKey())
              .orderId(res.getOrderId())
              .totalAmount(BigDecimal.valueOf(res.getTotalAmount()))
              .build());
    }
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class TossPaymentResponseError {
    private String code;
    private String message;
  }

  @AllArgsConstructor
  public static class TossConfirmRequestBody {

    private String privateKey;
    private String orderId;
    private String amount;

    public static TossConfirmRequestBody from(TossPaymentToken token) {
      return new TossConfirmRequestBody(token.getPaymentKey(), token.getOrderId(), token.getAmount());
    }
  }

  @AllArgsConstructor
  public static class TossCancelRequestBody {

    private String cancelReason;

    public static TossCancelRequestBody from() {
      return new TossCancelRequestBody("구매자가 취소를 원함.");
    }
  }
}
