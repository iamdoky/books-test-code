package com.books.external.api.payload.response.aladin;

public record AladinSearchResponse(

        String title,
        String link,
        String author,
        String pubDate,
        String description,
        String isbn,
        String isbn13,
        long itemId,
        long priceSales,
        long priceStandard,
        String mallType,
        String stockStatus,
        long mileage,
        String cover,
        long categoryId,
        String categoryName,
        String publisher,
        long salesPoint,
        boolean adult,
        boolean fixedPrice,
        long customerReviewRank,
        SeriesInfo seriesInfo,
        SubInfo subInfo) {
}