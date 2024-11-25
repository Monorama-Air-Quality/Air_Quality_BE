package com.sungjin.airquailitymonitordemo.dto.response;

//data class DeviceRegistrationResponse(
//        val success: Boolean,
//        val message: String
//)

public record DeviceRegistrationResponseDto(
        Boolean success,
        String message
) {
}
