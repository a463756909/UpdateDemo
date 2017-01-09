package com.inwatch.update;

public interface ParseData {
    <T> T parse(String httpResponse);
}
