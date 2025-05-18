package com.uguimar.notificationsms.infrastructure.adapter.input.grpc;

import com.uguimar.notificationsms.application.port.input.PasswordResetNotificationUseCase;
import com.uguimar.notificationsms.application.service.PasswordResetNotificationService;
import com.uguimar.notificationsms.infrastructure.grpc.NotificationServiceGrpc;
import com.uguimar.notificationsms.infrastructure.grpc.PasswordResetRequest;
import com.uguimar.notificationsms.infrastructure.grpc.PasswordResetResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import reactor.core.publisher.Mono;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class NotificationGrpcService extends NotificationServiceGrpc.NotificationServiceImplBase{

    private final PasswordResetNotificationService passwordResetService;


    @Override
    public void sendPasswordResetEmail(PasswordResetRequest request, StreamObserver<PasswordResetResponse> responseObserver) {
        passwordResetService.sendPasswordResetEmail(request.getEmail(), request.getCode()) // data y espera
                .subscribe(
                        result -> {
                            PasswordResetResponse response = PasswordResetResponse.newBuilder()
                                    .setMessage(result)
                                    .setStatus("200")
                                    .build();

                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> {
                            responseObserver.onNext(PasswordResetResponse.newBuilder()
                                    .setMessage("Error: " + error.getMessage())
                                    .setStatus("ERROR")
                                    .build());
                            responseObserver.onCompleted();
                        }
                );

    }
}
