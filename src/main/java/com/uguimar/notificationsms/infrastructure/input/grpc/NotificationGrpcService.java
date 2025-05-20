package com.uguimar.notificationsms.infrastructure.input.grpc;


import io.grpc.stub.StreamObserver;
import reactor.core.publisher.Mono;

import org.springframework.grpc.server.service.GrpcService;

import com.uguimar.notificationsms.application.port.input.SendVerificationEmailUseCase;
import com.uguimar.notificationsms.grpc.NotificationServiceGrpc;
import com.uguimar.notificationsms.grpc.SendNotificationEmailRequest;
import com.uguimar.notificationsms.grpc.SendNotificationEmailResponse;
@GrpcService //Notacion para el servivio gRPC
public class NotificationGrpcService extends NotificationServiceGrpc.NotificationServiceImplBase {

    //Creamos el atributo del servicio
    private final SendVerificationEmailUseCase sendVerificationEmailUseCase;

        //Constructor del servicio
        public NotificationGrpcService(SendVerificationEmailUseCase sendVerificationEmailUseCase) {
            this.sendVerificationEmailUseCase = sendVerificationEmailUseCase;
        }

        //Sobreescribimos el metodo del gRPC
        @Override
        public void sendNotificationEmail(SendNotificationEmailRequest request, StreamObserver<SendNotificationEmailResponse> responseObserver) {

            Mono<Void> result = sendVerificationEmailUseCase.send(request.getEmail(), request.getCode());

            //Ejecutamos el servicio
                result.subscribe(
                    //Si todo logra ejecutarse bien envia un true
                        unused -> {
                            SendNotificationEmailResponse response = SendNotificationEmailResponse.newBuilder()
                                .setSuccess(true)
                                .build();
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                    //Si no se ejecuta bien o algo falla envia un false
                        error -> {
                            SendNotificationEmailResponse response = SendNotificationEmailResponse.newBuilder()
                                .setSuccess(false)
                                .build ();
                            responseObserver.onNext( response );
                            responseObserver.onCompleted();
                        }
                );
        }
    
}
