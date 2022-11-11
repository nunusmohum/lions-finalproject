package com.ll.ebookmarket.app.order.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.ebookmarket.app.AppConfig;
import com.ll.ebookmarket.app.base.rq.Rq;
import com.ll.ebookmarket.app.member.entity.Member;
import com.ll.ebookmarket.app.member.service.MemberService;
import com.ll.ebookmarket.app.order.entity.Order;
import com.ll.ebookmarket.app.order.exception.ActorCanNotPayOrderException;
import com.ll.ebookmarket.app.order.exception.ActorCanNotSeeOrderException;
import com.ll.ebookmarket.app.order.exception.OrderIdNotMatchedException;
import com.ll.ebookmarket.app.order.exception.OrderNotEnoughRestCashException;
import com.ll.ebookmarket.app.order.service.OrderService;
import com.ll.ebookmarket.app.security.dto.MemberContext;
import com.ll.ebookmarket.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final MemberService memberService;
    private final Rq rq;

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String showDetail(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id, Model model) {
        Order order = orderService.findById(id).get();

        Member actor = memberContext.getMember();

        long restCash = memberService.getRestCash(actor);

        if (!orderService.actorCanSee(actor, order)) {
            throw new ActorCanNotSeeOrderException();
        }

        model.addAttribute("order", order);
        model.addAttribute("actorRestCash", restCash);

        return "order/detail";
    }

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String showList(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Member buyer = memberContext.getMember();
        List<Order> orders = orderService.findAll(buyer);

        model.addAttribute("orders", orders);

        return "order/list";
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String create(@AuthenticationPrincipal MemberContext memberContext) {
        Member member = memberContext.getMember();
        Order order = orderService.createFromCart(member);

        return "redirect:/order/%d".formatted(order.getId()) + "?msg=" + Ut.url.encode("%d번 주문이 생성되었습니다.".formatted(order.getId()));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public String cancel(@AuthenticationPrincipal MemberContext memberContext, @PathVariable Long id) {
        Member member = memberContext.getMember();
        Optional<Order> order = orderService.findById(id);
        if(order.isEmpty()){
            return rq.historyBack("존재하지 않는 주문입니다.");
        }
        if(!order.get().getBuyer().equals(member)){
            return rq.historyBack("요청 하실 수 없습니다.");
        }
        if(!order.get().isPayable()){
            return rq.historyBack("주문취소가 불가능합니다.");
        }
        orderService.cancel(order.get());

        return "redirect:/order/%d".formatted(id) + "?msg=" + Ut.url.encode("%d번 주문이 취소되었습니다.".formatted(id));
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("isAuthenticated()")
    public String refund(@AuthenticationPrincipal MemberContext memberContext, @PathVariable Long id) {
        Member member = memberContext.getMember();
        Optional<Order> order = orderService.findById(id);
        if(order.isEmpty()){
            return rq.historyBack("존재하지 않는 주문입니다.");
        }
        if(!order.get().getBuyer().equals(member)){
            return rq.historyBack("요청 하실 수 없습니다.");
        }
        if(!order.get().isRefundable()){
            return rq.historyBack("환불이 불가능합니다.");
        }

        orderService.refund(order.get());
        return "redirect:/order/%d".formatted(id) + "?msg=" + Ut.url.encode("%d번 주문이 환불되었습니다.".formatted(id));
    }

    @PostConstruct
    private void init() {
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
            }
        });
    }

    @PostMapping("/{id}/payByRestCashOnly")
    @PreAuthorize("isAuthenticated()")
    public String payByRestCashOnly(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id) {
        Order order = orderService.findById(id).get();

        Member actor = memberContext.getMember();

        long restCash = memberService.getRestCash(actor);

        if (!orderService.actorCanPayment(actor, order)) {
            throw new ActorCanNotPayOrderException();
        }

        orderService.payByRestCashOnly(order);

        return "redirect:/order/%d?msg=%s".formatted(order.getId(), Ut.url.encode("예치금으로 결제했습니다."));
    }

    @RequestMapping("/{id}/success")
    public String confirmPayment(
            @PathVariable long id,
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            Model model,
            @AuthenticationPrincipal MemberContext memberContext
    ) throws Exception {

        Order order = orderService.findById(id).get();

        long orderIdInputed = Long.parseLong(orderId.split("__")[1]);

        if (id != orderIdInputed) {
            throw new OrderIdNotMatchedException();
        }

        HttpHeaders headers = new HttpHeaders();
        // headers.setBasicAuth(SECRET_KEY, ""); // spring framework 5.2 이상 버전에서 지원
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((AppConfig.getSecretKey() + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(amount));

        Member actor = memberContext.getMember();
        long restCash = memberService.getRestCash(actor);
        long payPriceRestCash = order.calculatePayPrice() - amount;

        if (payPriceRestCash > restCash) {
            throw new OrderNotEnoughRestCashException();
        }

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {

            orderService.payByTossPayments(order, payPriceRestCash);

            return "redirect:/order/%d?msg=%s".formatted(order.getId(), Ut.url.encode("결제가 완료되었습니다."));
        } else {
            JsonNode failNode = responseEntity.getBody();
            model.addAttribute("message", failNode.get("message").asText());
            model.addAttribute("code", failNode.get("code").asText());
            return "order/fail";
        }
    }

    @RequestMapping("/{id}/fail")
    public String failPayment(@RequestParam String message, @RequestParam String code, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "order/fail";
    }
}
