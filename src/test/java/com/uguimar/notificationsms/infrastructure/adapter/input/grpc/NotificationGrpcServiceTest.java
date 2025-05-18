package com.uguimar.notificationsms.infrastructure.adapter.input.grpc;

import com.uguimar.notificationsms.application.port.input.PasswordResetNotificationUseCase;
import com.uguimar.notificationsms.infrastructure.grpc.PasswordResetRequest;
import com.uguimar.notificationsms.infrastructure.grpc.PasswordResetResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationGrpcServiceTest {

    @Mock
    private PasswordResetNotificationUseCase passwordResetNotificationUseCase;

    @Mock
    private StreamObserver<PasswordResetResponse> responseObserver;

    @InjectMocks
    private NotificationGrpcService grpcService;

    @Captor
    private ArgumentCaptor<PasswordResetResponse> responseCaptor;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_CODE = "123456";
    private static final String SUCCESS_RESPONSE = "Correo enviado exitosamente";

    private PasswordResetRequest request;

    @BeforeEach
    void setUp() {
        request = PasswordResetRequest.newBuilder()
                .setEmail(TEST_EMAIL)
                .setCode(TEST_CODE)
                .build();

        when(passwordResetNotificationUseCase.sendPasswordResetEmail(anyString(), anyString()))
                .thenReturn(Mono.just(SUCCESS_RESPONSE));
    }

    @Test
    void sendPasswordResetEmail_Success() {
        grpcService.sendPasswordResetEmail(request, responseObserver);

        verify(passwordResetNotificationUseCase).sendPasswordResetEmail(TEST_EMAIL, TEST_CODE);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        PasswordResetResponse capturedResponse = responseCaptor.getValue();
        assertEquals(SUCCESS_RESPONSE, capturedResponse.getStatus());
    }

    @Test
    void sendPasswordResetEmail_Error() {
        RuntimeException testException = new RuntimeException("Test Error");
        when(passwordResetNotificationUseCase.sendPasswordResetEmail(anyString(), anyString()))
                .thenReturn(Mono.error(testException));

        grpcService.sendPasswordResetEmail(request, responseObserver);

        verify(passwordResetNotificationUseCase).sendPasswordResetEmail(TEST_EMAIL, TEST_CODE);
        verify(responseObserver).onError(testException);
        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, never()).onCompleted();
    }
}
