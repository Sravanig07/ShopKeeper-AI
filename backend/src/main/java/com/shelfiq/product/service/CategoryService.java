package com.shelfiq.product.service;

import com.shelfiq.common.context.TenantContextHolder;
import com.shelfiq.common.exception.ResourceNotFoundException;
import com.shelfiq.product.dto.CategoryDto;
import com.shelfiq.product.entity.Category;
import com.shelfiq.product.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoriesForCurrentStore() {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        List<Category> categories = categoryRepository.findAllAvailableForStore(storeId);
        return categories.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto dto) {
        Long storeId = TenantContextHolder.getRequiredStoreId();

        Category parent = null;
        if (dto.getParentId() != null) {
            parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getParentId()));
        }

        Category category = new Category(storeId, dto.getName(), dto.getDescription(), parent);
        Category saved = categoryRepository.save(category);
        return toDto(saved);
    }

    @Transactional
    public void seedDefaultMasterCategories() {
        if (categoryRepository.count() == 0) {
            String[][] defaultCats = {
                    {"Beverages", "Carbonated drinks, juices, energy drinks, tea, coffee"},
                    {"Snacks & Packaged Foods", "Chips, biscuits, noodles, chocolates, confectionery"},
                    {"Dairy & Eggs", "Milk, yogurt, cheese, butter, eggs"},
                    {"Bakery & Breads", "Fresh bread, buns, pastries, baked goods"},
                    {"Personal Care", "Soaps, shampoos, oral care, cosmetics"},
                    {"Household & Cleaning", "Detergents, cleaners, tissue paper, dishwashing"},
                    {"Grains & Staples", "Rice, wheat flour, pulses, spices, edible oils"},
                    {"Frozen & Ice Cream", "Ice creams, frozen snacks, ready-to-eat meals"}
            };
            for (String[] cat : defaultCats) {
                categoryRepository.save(new Category(null, cat[0], cat[1], null));
            }
        }
    }

    public CategoryDto toDto(Category c) {
        return new CategoryDto(
                c.getId(),
                c.getName(),
                c.getDescription(),
                c.getParentCategory() != null ? c.getParentCategory().getId() : null,
                c.getParentCategory() != null ? c.getParentCategory().getName() : null
        );
    }
}
