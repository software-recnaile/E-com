package com.recnaile.productService.service;//package com.recnaile.productService.service;
//
//import com.cloudinary.Cloudinary;
//import com.cloudinary.utils.ObjectUtils;
//import com.recnaile.productService.model.Product;
//import com.recnaile.productService.repository.ProductRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//
//@Service
//@RequiredArgsConstructor
//public class ProductService {
//
//    private final ProductRepository productRepository;
//    private final Cloudinary cloudinary;
//
//    public Product createProduct(Product product, MultipartFile[] imageFiles) throws IOException {
//        if (imageFiles != null && imageFiles.length > 0) {
//            List<String> imageUrls = new ArrayList<>();
//            for (MultipartFile file : imageFiles) {
//                if (!file.isEmpty()) {
//                    String imageUrl = uploadImageToCloudinary(file);
//                    imageUrls.add(imageUrl);
//                }
//            }
//            product.setImageUrls(imageUrls);
//        }
//        return productRepository.save(product);
//    }
//
//    public Product getProductByUniqueName(String productUniqueName) {
//        return productRepository.findByProductUniqueName(productUniqueName)
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//    }
//
//    public List<Product> getAllProducts() {
//        return productRepository.findAll();
//    }
//
//    private String uploadImageToCloudinary(MultipartFile file) throws IOException {
//        File uploadedFile = convertMultiPartToFile(file);
//        Map uploadResult = cloudinary.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
//        boolean isDeleted = uploadedFile.delete();
//
//        if (uploadResult.get("url") != null) {
//            return uploadResult.get("url").toString();
//        } else {
//            throw new RuntimeException("Image upload failed");
//        }
//    }
//
//    private File convertMultiPartToFile(MultipartFile file) throws IOException {
//        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
//        FileOutputStream fos = new FileOutputStream(convFile);
//        fos.write(file.getBytes());
//        fos.close();
//        return convFile;
//    }
//
//    // Add update and delete methods as needed
//}

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.recnaile.productService.model.Product;
import com.recnaile.productService.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final Cloudinary cloudinary;

    // Create product with images
    public Product createProduct(String productUniqueName, MultipartFile[] imageFiles) throws IOException {
        if (productRepository.existsByProductUniqueName(productUniqueName)) {
            throw new RuntimeException("Product with this unique name already exists");
        }

        Product product = new Product();
        product.setProductUniqueName(productUniqueName);

        if (imageFiles != null && imageFiles.length > 0) {
            product.setImageUrls(uploadImages(imageFiles));
        }

        return productRepository.save(product);
    }

    // Get product by unique name
    public Product getProduct(String productUniqueName) {
        return productRepository.findByProductUniqueName(productUniqueName)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // Update product images
    public Product updateProductImages(String productUniqueName, MultipartFile[] newImages) throws IOException {
        Product product = getProduct(productUniqueName);

        // Delete old images if they exist
        if (product.getImageUrls() != null) {
            deleteImages(product.getImageUrls());
        }

        // Upload and set new images
        if (newImages != null && newImages.length > 0) {
            product.setImageUrls(uploadImages(newImages));
        } else {
            product.setImageUrls(null);
        }

        return productRepository.save(product);
    }

    // Delete product and its images
    public void deleteProduct(String productUniqueName) throws IOException {
        Product product = getProduct(productUniqueName);

        // Delete images from Cloudinary
        if (product.getImageUrls() != null) {
            deleteImages(product.getImageUrls());
        }

        productRepository.delete(product);
    }

    // Helper method to upload images
    private List<String> uploadImages(MultipartFile[] files) throws IOException {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                File uploadedFile = convertToFile(file);
                Map uploadResult = cloudinary.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
                uploadedFile.delete();

                if (uploadResult.get("url") != null) {
                    urls.add(uploadResult.get("url").toString());
                }
            }
        }
        return urls;
    }

    // Helper method to delete images
    private void deleteImages(List<String> imageUrls) throws IOException {
        for (String url : imageUrls) {
            String publicId = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }

    private File convertToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public String extractPublicId(String imageUrl) {
        // Example: https://res.cloudinary.com/duy54uvmo/image/upload/v1753676154/wzszkwcm0cx5zl7anoyw.png
        int lastSlash = imageUrl.lastIndexOf('/');
        int dotIndex = imageUrl.lastIndexOf('.');

        if (lastSlash == -1 || dotIndex == -1) return null;
        return imageUrl.substring(lastSlash + 1, dotIndex); // returns 'wzszkwcm0cx5zl7anoyw'
    }

    // Update single image by index
    public Product updateSingleImage(String productUniqueName, int imageIndex, MultipartFile newImage) throws IOException {
        Product product = getProduct(productUniqueName);

        if (product.getImageUrls() == null || product.getImageUrls().isEmpty()) {
            throw new RuntimeException("Product has no images to update");
        }

        if (imageIndex < 0 || imageIndex >= product.getImageUrls().size()) {
            throw new RuntimeException("Invalid image index");
        }

        // Delete the old image from Cloudinary
        String oldImageUrl = product.getImageUrls().get(imageIndex);
        deleteImage(oldImageUrl);

        // Upload the new image
        String newImageUrl = uploadImage(newImage);

        // Update the image URL at the specified index
        product.getImageUrls().set(imageIndex, newImageUrl);

        return productRepository.save(product);
    }

    // Helper method to upload single image
    private String uploadImage(MultipartFile file) throws IOException {
        File uploadedFile = convertToFile(file);
        Map uploadResult = cloudinary.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
        uploadedFile.delete();

        if (uploadResult.get("url") == null) {
            throw new RuntimeException("Image upload failed");
        }
        return uploadResult.get("url").toString();
    }


    // Helper method to delete single image
    private void deleteImage(String imageUrl) throws IOException {
        String publicId = extractPublicId(imageUrl);
        Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

    }
}