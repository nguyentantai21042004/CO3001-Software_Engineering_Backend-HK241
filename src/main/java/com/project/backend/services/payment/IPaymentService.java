package com.project.backend.services.payment;

import com.project.backend.responses.ResponseObject;

public interface IPaymentService {

    ResponseObject createPayment(Integer studentId, int a4Count, int a3Count, String method, String description);

    ResponseObject findAllPayments();

    ResponseObject findPaymentById(Integer id);

    ResponseObject findPaymentByStudentId(Integer studentId);

}
