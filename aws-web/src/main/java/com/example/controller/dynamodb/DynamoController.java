package com.example.controller.dynamodb;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.dynamodb.DynamoDbService;
import com.example.dynamodb.MusicRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/dynamodb")
public class DynamoController {

    @Autowired
    private DynamoDbService dynamoDbService;
    @PostMapping(value = "/{tableName}")
    public ResponseEntity<String> createDynamoDbTable(@PathVariable("tableName") String tableName, @RequestParam("key") String key) {
        return dynamoDbService.createTable(tableName, key);
    }
    @GetMapping
    public ResponseEntity<Object> getItem(@RequestParam("key") String key,@RequestParam("tableName") String tableName ) {
        return dynamoDbService.getItems(key, tableName);
    }
    @PostMapping(value = "/add")
    public ResponseEntity<Object> addItem(@RequestParam("name") String name,@RequestBody Map<String, AttributeValue> extraItems, @RequestParam("tableName") String tableName) {
        return dynamoDbService.addItems(name,extraItems, tableName);
    }
    @GetMapping(value = "/list")
    public ResponseEntity<List<String>> listTable() {
        return dynamoDbService.listTable();
    }

    // this Add item adds to the dynamodb via using Java class
    @PostMapping(value = "/add-item")
    public ResponseEntity<Object> addItem(@RequestBody MusicRequest musicRequest) {
        return dynamoDbService.addItemViaClass(musicRequest);
    }
    @PutMapping(value = "/item")
    public ResponseEntity<Object> updateItem(@RequestParam("tableName") String tableName, @RequestParam("name") String name, @RequestBody Map<String, AttributeValue> item) {
        return dynamoDbService.updateExistingItem(name, item, tableName);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteTable(@RequestParam("tableName") String tableName) {
        return dynamoDbService.deleteTable(tableName);
    }

    @DeleteMapping(value = "/item")
    public ResponseEntity<String> deleteTable(@RequestParam("tableName") String tableName, @RequestParam("itemKey") String itemKey) {
        return dynamoDbService.deleteItem(tableName, itemKey);
    }

}
