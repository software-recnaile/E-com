package com.recnaile.productService.model;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Document(collection = "images")
public class Product {
    @Id
    private String id;


    private String productUniqueName;

//    private String name;
//    private String description;
//    private double price;
    private List<String> imageUrls;
}