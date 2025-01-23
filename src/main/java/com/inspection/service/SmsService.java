// package com.inspection.service;

// import org.springframework.stereotype.Service;

// import com.inspection.config.TwilioConfig;
// import com.twilio.Twilio;
// import com.twilio.rest.api.v2010.account.Message;

// import jakarta.annotation.PostConstruct;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class SmsService {
//     private final TwilioConfig twilioConfig;

//     @PostConstruct
//     public void init() {
//         Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
//     }

//     public void sendSms(String to, String messageBody) throws Exception {
//         log.info("Sending SMS to: {}", to);
//         try {
//             String formattedNumber = to.trim().replaceAll("\\s+", "");
//             if (!formattedNumber.startsWith("+")) {
//                 formattedNumber = "+" + formattedNumber;
//             }
//             log.info("Formatted number: {}", formattedNumber);
            
//             Message message = Message.creator(
//                 new com.twilio.type.PhoneNumber(formattedNumber),
//                 new com.twilio.type.PhoneNumber(twilioConfig.getPhoneNumber()),
//                 messageBody
//             )
//             .create();
//             log.info("Message SID: {}", message.getSid());
//             log.info("SMS sent successfully");
//         } catch (Exception e) {
//             log.error("Failed to send SMS: {}", e.getMessage());
//             throw new Exception("SMS 전송에 실패했습니다: " + e.getMessage());
//         }
//     }
// } 