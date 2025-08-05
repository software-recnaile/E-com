    package com.recnaile.productService.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.recnaile.productService.dto.ProductDTO;
import com.recnaile.productService.dto.ProductResponse;
import com.recnaile.productService.exception.ProductNotFoundException;
import com.recnaile.productService.model.Product;
import com.recnaile.productService.repository.ProductRepository;
import com.recnaile.productService.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final Cloudinary cloudinary;
     private static final String PRODUCT_SERVICE_URL = "https://product-service-4psw.onrender.com/api/products/";
    

    @Override
    public ProductResponse createProduct(ProductDTO productDTO, MultipartFile[] images) throws IOException {
        String uniqueProductName = generateUniqueProductName();

        Product product = modelMapper.map(productDTO, Product.class);
        product.setUniqueProductName(uniqueProductName);
        product.setRatings(0.0);
        product.setCreatedAt(LocalDateTime.now());
        product.setEditedAt(LocalDateTime.now());

        if (images != null && images.length > 0) {
            product.setImageUrls(uploadImages(images));
        }

        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return mapToProductResponse(product);
    }

    @Override
    public ProductResponse getProductByUniqueName(String uniqueName) {
        Product product = productRepository.findByUniqueProductName(uniqueName)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with unique name: " + uniqueName));
        return mapToProductResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

   
    


    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        List<ProductResponse> responses = productPage.getContent()
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, productPage.getTotalElements());
    }

    @Override
    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByProductCategory(category)
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByCategoryAndSubCategory(String category, String subCategory) {
        return productRepository.findByProductCategoryAndProductSubCategory(category, subCategory)
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> searchProducts(String query) {
        return productRepository.findByProductNameContainingIgnoreCase(query)
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse updateProduct(String id, ProductDTO productDTO, MultipartFile[] images) throws IOException {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        // Preserve existing images if no new images are provided
        List<String> existingImageUrls = existingProduct.getImageUrls();

        modelMapper.map(productDTO, existingProduct);
        existingProduct.setEditedAt(LocalDateTime.now());

        if (images != null && images.length > 0) {
            // Delete old images if they exist
            if (existingImageUrls != null && !existingImageUrls.isEmpty()) {
                deleteImages(existingImageUrls);
            }
            existingProduct.setImageUrls(uploadImages(images));
        } else {
            // Keep existing images if no new images are provided
            existingProduct.setImageUrls(existingImageUrls);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToProductResponse(updatedProduct);
    }
    @Override
    public void deleteProduct(String id) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        // Delete images from Cloudinary
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            deleteImages(product.getImageUrls());
        }

        productRepository.delete(product);
    }

    @Override
    public ProductResponse updateStock(String id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        product.setAvailableStock(product.getAvailableStock() + quantity);
        product.setEditedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    @Override
    public ProductResponse addProductImages(String id, MultipartFile[] images) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        if (images != null && images.length > 0) {
            List<String> newImageUrls = uploadImages(images);
            if (product.getImageUrls() == null) {
                product.setImageUrls(newImageUrls);
            } else {
                product.getImageUrls().addAll(newImageUrls);
            }
            product.setEditedAt(LocalDateTime.now());
        }

        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    @Override
    public ProductResponse updateSingleImage(String id, int imageIndex, MultipartFile image) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        if (product.getImageUrls() == null || product.getImageUrls().isEmpty()) {
            throw new ProductNotFoundException("Product has no images to update");
        }

        if (imageIndex < 0 || imageIndex >= product.getImageUrls().size()) {
            throw new IllegalArgumentException("Invalid image index");
        }

        // Delete the old image from Cloudinary
        String oldImageUrl = product.getImageUrls().get(imageIndex);
        deleteImage(oldImageUrl);

        // Upload the new image
        String newImageUrl = uploadImage(image);

        // Update the image URL at the specified index
        product.getImageUrls().set(imageIndex, newImageUrl);
        product.setEditedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    @Override
    public void deleteProductImages(String id) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            deleteImages(product.getImageUrls());
            product.setImageUrls(null);
            product.setEditedAt(LocalDateTime.now());
            productRepository.save(product);
        }
    }

    // Helper methods
    private List<String> uploadImages(MultipartFile[] files) throws IOException {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                urls.add(uploadImage(file));
            }
        }
        return urls;
    }

    private String uploadImage(MultipartFile file) throws IOException {
        File uploadedFile = convertToFile(file);
        Map uploadResult = cloudinary.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
        uploadedFile.delete();

        if (uploadResult.get("url") == null) {
            throw new IOException("Failed to upload image to Cloudinary");
        }
        return uploadResult.get("url").toString();
    }

    private void deleteImages(List<String> imageUrls) throws IOException {
        for (String url : imageUrls) {
            deleteImage(url);
        }
    }

    private void deleteImage(String imageUrl) throws IOException {
        String publicId = extractPublicId(imageUrl);
        if (publicId != null) {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }

    private String extractPublicId(String imageUrl) {
        // Example URL: https://res.cloudinary.com/demo/image/upload/v1234567/folder/file.jpg
        try {
            String[] parts = imageUrl.split("/upload/")[1].split("/");
            String path = String.join("/", Arrays.copyOfRange(parts, 1, parts.length));
            return path.substring(0, path.lastIndexOf('.'));
        } catch (Exception e) {
            return null;
        }
    }

    private File convertToFile(MultipartFile file) throws IOException {
        File convFile = File.createTempFile("temp", Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    private String generateUniqueProductName() {
        String uuid = UUID.randomUUID().toString();
        String digits = uuid.replaceAll("[^0-9]", "");
        return digits.length() < 6 ?
                String.format("%06d", new Random().nextInt(999999)) :
                digits.substring(0, 6);
    }

    private ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = modelMapper.map(product, ProductResponse.class);






        // Calculate discounted price if discount exists
        if (product.getDiscountAmount() != null && product.getDiscountAmount() > 0) {
            response.setDiscountedPrice(product.getMrpRate() - product.getDiscountAmount());
        }

        return response;
    }
    @Override
    public void deleteSingleImage(String id, int imageIndex) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        if (product.getImageUrls() == null || product.getImageUrls().isEmpty()) {
            throw new ProductNotFoundException("Product has no images to delete");
        }

        if (imageIndex < 0 || imageIndex >= product.getImageUrls().size()) {
            throw new IllegalArgumentException("Invalid image index");
        }

        // Get the image URL to delete
        String imageUrl = product.getImageUrls().get(imageIndex);

        // Delete from Cloudinary
        deleteImage(imageUrl);

        // Remove from the list
        product.getImageUrls().remove(imageIndex);
        product.setEditedAt(LocalDateTime.now());

        productRepository.save(product);
    }


   @Override
public ProductResponse updateStockByUniqueName(String uniqueName, Integer quantity) {
    Product product = productRepository.findByUniqueProductName(uniqueName)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with unique name: " + uniqueName));

    product.setAvailableStock(product.getAvailableStock() + quantity);
    product.setEditedAt(LocalDateTime.now());

    Product updatedProduct = productRepository.save(product);
    return mapToProductResponse(updatedProduct);
}

      private void updateProductStock(String uniqueProductName, int quantityChange) {
        try {
            String url = PRODUCT_SERVICE_URL + "unique/" + uniqueProductName + "/stock?quantity=" + quantityChange;
            restTemplate.patchForObject(url, null, ProductResponse.class);
        } catch (Exception e) {
            System.err.println("Failed to update product stock: " + e.getMessage());
            // Handle error appropriately
        }
    }


    
}

