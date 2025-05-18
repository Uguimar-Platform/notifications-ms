package com.uguimar.notificationsms.infrastructure.adapter.input.grpc;
import com.uguimar.notificationsms.application.service.PasswordResetNotificationService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequestMapping("/notifications")
@RestController
public class PasswordResetNotificationController {

    private final PasswordResetNotificationService passwordResetNotificationService;

    public PasswordResetNotificationController(PasswordResetNotificationService passwordResetNotificationService) {
        this.passwordResetNotificationService = passwordResetNotificationService;
    }

    @PostMapping("/password-reset/{emailReceptor}/{code}")
  public Mono<String> sendPasswordResetEmail(@PathVariable String emailReceptor, @PathVariable String code) {
    return passwordResetNotificationService.sendPasswordResetEmail(emailReceptor, code);
  }



}
