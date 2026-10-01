package com.shelfiq.supplier.service;

import com.shelfiq.common.context.TenantContextHolder;
import com.shelfiq.common.exception.ResourceNotFoundException;
import com.shelfiq.supplier.dto.SupplierDto;
import com.shelfiq.supplier.entity.Supplier;
import com.shelfiq.supplier.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Transactional(readOnly = true)
    public List<SupplierDto> getAllSuppliers() {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        return supplierRepository.findByStoreId(storeId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SupplierDto getSupplierById(Long id) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        Supplier supplier = supplierRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        return toDto(supplier);
    }

    @Transactional
    public SupplierDto createSupplier(SupplierDto dto) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        Supplier supplier = new Supplier(
                storeId,
                dto.getName(),
                dto.getContactPerson(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getLeadTimeDays(),
                dto.getReliabilityScore()
        );
        Supplier saved = supplierRepository.save(supplier);
        return toDto(saved);
    }

    @Transactional
    public SupplierDto updateSupplier(Long id, SupplierDto dto) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        Supplier supplier = supplierRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));

        supplier.setName(dto.getName());
        supplier.setContactPerson(dto.getContactPerson());
        supplier.setEmail(dto.getEmail());
        supplier.setPhone(dto.getPhone());
        supplier.setLeadTimeDays(dto.getLeadTimeDays());
        supplier.setReliabilityScore(dto.getReliabilityScore());

        Supplier updated = supplierRepository.save(supplier);
        return toDto(updated);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        Supplier supplier = supplierRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        supplierRepository.delete(supplier);
    }

    private SupplierDto toDto(Supplier s) {
        return new SupplierDto(
                s.getId(),
                s.getName(),
                s.getContactPerson(),
                s.getEmail(),
                s.getPhone(),
                s.getLeadTimeDays(),
                s.getReliabilityScore()
        );
    }
}
