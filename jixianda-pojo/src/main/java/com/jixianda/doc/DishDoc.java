package com.jixianda.doc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "dish")
public class DishDoc {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String name;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Keyword)
    private String image;

    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Nested, includeInParent = true)
    @Builder.Default
    private List<WarehouseStock> warehouseStock = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseStock {

        @Field(type = FieldType.Long)
        private Long warehouseId;

        @Field(type = FieldType.Integer)
        private Integer stock;
    }
}
