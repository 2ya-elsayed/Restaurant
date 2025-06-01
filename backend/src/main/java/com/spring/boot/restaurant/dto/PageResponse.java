package com.spring.boot.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private long totalElements;     // The total number of items across all pages
    private int numberOfElements;   // The number of elements returned in the current page
    private int pageNumber;         // The current page number
    private int totalPages;         // The total number of pages available based on the pageSize and totalElements
    private int pageSize;           // The number of elements requested or configured per page.
}
