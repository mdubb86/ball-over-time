package com.ballovertime.datagen.model;

public record CachedPage(String url, int code, byte[] content, long timestamp) {}
