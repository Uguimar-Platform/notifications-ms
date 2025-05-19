package com.uguimar.notificationsms.infrastructure.adapter.input.grpc;

import com.uguimar.notification.grpc.NotificationServiceGrpc;
import com.uguimar.notification.grpc.PasswordResetCodeRequest;
import com.uguimar.notification.grpc.PasswordResetCodeResponse;
import com.uguimar.notificationsms.application.port.input.PasswordResetNotificationUseCase;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class NotificationGrpcService extends NotificationServiceGrpc.NotificationServiceImplBase {

    private final PasswordResetNotificationUseCase passwordResetNotificationService;

    @Override
    public void sendPasswordResetCode(PasswordResetCodeRequest request, StreamObserver<PasswordResetCodeResponse> responseObserver) {
        log.info("Recibida solicitud de restablecimiento de contraseña para: {}", request.getEmail());

        passwordResetNotificationService.sendPasswordResetEmail(request.getEmail(), request.getUsername(), request.getResetCode())
                .subscribe(
                        result -> {
                            PasswordResetCodeResponse response = PasswordResetCodeResponse.newBuilder()
                                    .setSuccess(true)
                                    .setMessage(result)
                                    .build();

                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                            log.info("Código de restablecimiento de contraseña enviado exitosamente a: {}", request.getEmail());
                        },
                        error -> {
                            log.error("Error al enviar código de restablecimiento a {}: {}", request.getEmail(), error.getMessage());
                            PasswordResetCodeResponse response = PasswordResetCodeResponse.newBuilder()
                                    .setSuccess(false)
                                    .setMessage("Error: " + error.getMessage())
                                    .build();
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        }
                );
    }
}