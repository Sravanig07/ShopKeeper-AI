package com.shelfiq.store.service;

import com.shelfiq.common.context.TenantContextHolder;
import com.shelfiq.common.exception.ResourceNotFoundException;
import com.shelfiq.store.dto.StoreResponseDto;
import com.shelfiq.store.dto.StoreUpdateDto;
import com.shelfiq.store.entity.Store;
import com.shelfiq.store.entity.StoreLocation;
import com.shelfiq.store.repository.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Transactional(readOnly = true)
    public StoreResponseDto getCurrentStore() {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", storeId));

        return toDto(store);
    }

    @Transactional
    public StoreResponseDto updateCurrentStore(StoreUpdateDto dto) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", storeId));

        store.setName(dto.getName());
        if (dto.getBusinessType() != null) store.setBusinessType(dto.getBusinessType());
        if (dto.getCurrency() != null) store.setCurrency(dto.getCurrency());
        if (dto.getTimezone() != null) store.setTimezone(dto.getTimezone());

        StoreLocation location = store.getLocation();
        if (location == null) {
            location = new StoreLocation();
            store.setLocation(location);
        }
        location.setAddressLine1(dto.getAddressLine1());
        location.setCity(dto.getCity());
        location.setState(dto.getState());
        location.setPostalCode(dto.getPostalCode());
        location.setLatitude(dto.getLatitude());
        location.setLongitude(dto.getLongitude());
        location.calculateGeohashes();

        Store updated = storeRepository.save(store);
        return toDto(updated);
    }

    private StoreResponseDto toDto(Store store) {
        StoreLocation loc = store.getLocation();
        return new StoreResponseDto(
                store.getId(),
                store.getStoreCode(),
                store.getName(),
                store.getBusinessType(),
                store.getCurrency(),
                store.getTimezone(),
                loc != null ? loc.getAddressLine1() : null,
                loc != null ? loc.getCity() : null,
                loc != null ? loc.getState() : null,
                loc != null ? loc.getPostalCode() : null,
                loc != null ? loc.getLatitude() : null,
                loc != null ? loc.getLongitude() : null,
                loc != null ? loc.getGeohash5() : null,
                loc != null ? loc.getGeohash6() : null
        );
    }
}
