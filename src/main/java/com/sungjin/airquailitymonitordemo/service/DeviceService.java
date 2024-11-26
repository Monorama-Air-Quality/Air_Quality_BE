package com.sungjin.airquailitymonitordemo.service;

import com.sungjin.airquailitymonitordemo.dto.request.DeviceRegistrationRequestDto;
import com.sungjin.airquailitymonitordemo.dto.response.DeviceRegistrationResponseDto;
import com.sungjin.airquailitymonitordemo.dto.response.DeviceResponseDto;
import com.sungjin.airquailitymonitordemo.entity.Device;
import com.sungjin.airquailitymonitordemo.entity.Project;
import com.sungjin.airquailitymonitordemo.repository.DeviceRepository;
import com.sungjin.airquailitymonitordemo.repository.ProjectRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final ProjectRepository projectRepository;

    public DeviceResponseDto getDevice(String deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new EntityNotFoundException("Device not found"));
        return convertToDto(device);
    }

    @Transactional
    public DeviceRegistrationResponseDto registerDevice(String deviceId, DeviceRegistrationRequestDto request) {
        log.info("Processing device registration/update for ID: {}", deviceId);

        boolean isNewDevice = !deviceRepository.existsById(deviceId);

        try {
            Project project = projectRepository.findById(request.projectId())
                    .orElseThrow(() -> new EntityNotFoundException("Project not found"));

            Device device = deviceRepository.findById(deviceId)
                    .map(existingDevice -> {
                        log.info("Updating existing device: {}", deviceId);
                        existingDevice.setUserName(request.userName());
                        existingDevice.setUserEmail(request.userEmail());
                        existingDevice.setProject(project);
                        return existingDevice;
                    })
                    .orElseGet(() -> {
                        log.info("Creating new device: {}", deviceId);
                        return Device.builder()
                                .deviceId(deviceId)
                                .userName(request.userName())
                                .userEmail(request.userEmail())
                                .project(project)
                                .build();
                    });

            deviceRepository.save(device);

            String message = isNewDevice
                    ? "Device registered successfully"
                    : "Device information updated successfully";

            log.info("Successfully {} device: {}", isNewDevice ? "registered" : "updated", deviceId);

            return new DeviceRegistrationResponseDto(true, message);

        } catch (EntityNotFoundException e) {
            log.error("Error processing device registration: {}", e.getMessage());
            return new DeviceRegistrationResponseDto(false, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during device registration", e);
            return new DeviceRegistrationResponseDto(false, "An unexpected error occurred");
        }
    }


    private DeviceResponseDto convertToDto(Device device) {
        return new DeviceResponseDto(
                device.getDeviceId(),
                device.getUserName(),
                device.getUserEmail(),
                device.getProject() != null ? device.getProject().getProjectId() : null
        );
    }
}