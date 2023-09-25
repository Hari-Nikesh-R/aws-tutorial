package com.example.dynamodb;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface DynamoDbService {
    ResponseEntity<String> createTable(String tableName, String key);
    ResponseEntity<List<String>> listTable();
    ResponseEntity<Object> getItems(String key, String tableName);
    ResponseEntity<Object> addItems(String name, Map<String, AttributeValue> extraFields, String tableName);
    ResponseEntity<Object> updateExistingItem(String name, Map<String, AttributeValue> item, String tableName);
    ResponseEntity<Object> addItemViaClass(MusicRequest musicRequest);
    ResponseEntity<String> deleteTable(String tableName);
    ResponseEntity<String> deleteItem(String tableName, String itemKey);
}
