package com.example.dynamodb;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.example.entity.MusicItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

//todo: Reference - https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/java/example_code/dynamodb/src/main/java/aws/example/dynamodb/UseDynamoMapping.java

@Service
public class DynamoDbServiceImpl implements DynamoDbService {

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Override
    public ResponseEntity<String> createTable(String tableName) {
        CreateTableRequest request = new CreateTableRequest()
                .withAttributeDefinitions(new AttributeDefinition(
                        "Name", ScalarAttributeType.S))
                .withKeySchema(new KeySchemaElement("Name", KeyType.HASH))
                .withProvisionedThroughput(new ProvisionedThroughput(
                        10L, 10L))
                .withTableName(tableName);
        try {
            CreateTableResult result = amazonDynamoDB.createTable(request);
            System.out.println(result.getTableDescription().getTableName());
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            ResponseEntity.ok("Table not created");
        }
        return ResponseEntity.ok("Table created");
    }

    @Override
    public ResponseEntity<List<String>> listTable() {

        ListTablesRequest request;

        String last_name = null;
        List<String> tableNames;

        while (true) {
            try {
                request = new ListTablesRequest().withLimit(10);

                ListTablesResult table_list = amazonDynamoDB.listTables(request);
                tableNames = table_list.getTableNames();

                if (tableNames.size() > 0) {
                    for (String cur_name : tableNames) {
                        System.out.format("* %s\n", cur_name);
                    }
                } else {
                    System.out.println("No tables found!");

                }

                last_name = table_list.getLastEvaluatedTableName();
                if (last_name == null) {
                    break;
                }

            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }

        }
        return ResponseEntity.ok(tableNames);
    }

    @Override
    public ResponseEntity<Object> getItems(String key, String tableName) {
      try {
          Map<String, AttributeValue> returned_item = getItemFromDb(tableName, key);
            if (returned_item != null) {
                Set<String> keys = returned_item.keySet();
                return ResponseEntity.ok(keys);
            } else {
                System.out.format("No item found with the key %s!\n", key);
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("No item found with the key %s!\n" + key);
            }
        } catch (AmazonServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getErrorMessage());
        }

    }



    @Override
    public ResponseEntity<Object> addItems(String name, Map<String, AttributeValue> extraFields, String tableName) {
        HashMap<String, AttributeValue> item_values =
                new HashMap<String, AttributeValue>();

        item_values.put("Name", new AttributeValue(name));

        try {
            amazonDynamoDB.putItem(tableName, extraFields);
            return ResponseEntity.ok("Item added successfully");
        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The table \"%s\" can't be found.\n", tableName);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Be sure that it exists and that you've typed its name correctly!");
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getErrorMessage());
        }
    }

    @Override
    public ResponseEntity<Object> updateExistingItem(String name, Map<String, AttributeValue> item, String tableName) {
        HashMap<String, AttributeValue> item_key =
                new HashMap<String, AttributeValue>();

        item_key.put("Name", new AttributeValue(name));

        HashMap<String, AttributeValueUpdate> updated_values =
                new HashMap<String, AttributeValueUpdate>();

        for (Map.Entry<String, AttributeValue> entry : item.entrySet()) {
            updated_values.put(entry.getKey(), new AttributeValueUpdate(
                    entry.getValue(), AttributeAction.PUT));
        }

        try {
            amazonDynamoDB.updateItem(tableName, item_key, updated_values);
            return ResponseEntity.ok("Item updated successfully");

        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getErrorMessage());

        }


    }

    @Override
    public ResponseEntity<Object> addItemViaClass(MusicRequest musicRequest) {
        MusicItems items = new MusicItems();

        try{
            // Add new content to the Music table
            items.setArtist(musicRequest.getArtist());
            items.setSongTitle(musicRequest.getSongTitle());
            items.setAlbumTitle(musicRequest.getAlbumTitle());
            items.setAwards(musicRequest.getAwards());

            // Save the item
            DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);
            mapper.save(items);

            // Load an item based on the Partition Key and Sort Key
            // Both values need to be passed to the mapper.load method
//            String artistName = musicRequest.getArtist();
//            String songQueryTitle = musicRequest.getSongTitle();

            // Retrieve the item
//            MusicItems itemRetrieved = mapper.load(MusicItems.class, artistName, songQueryTitle);
//            System.out.println("Item retrieved:");
//            System.out.println(itemRetrieved);
//
//            // Modify the Award value
//            itemRetrieved.setAwards(2);
//            mapper.save(itemRetrieved);
//            System.out.println("Item updated:");
//            System.out.println(itemRetrieved);
//
//            System.out.print("Done");
        } catch (AmazonDynamoDBException e) {
            e.getStackTrace();
        }
        return ResponseEntity.ok("All Crud operation is performed");
    }

    @Override
    public ResponseEntity<String> deleteTable(String tableName) {
        try {
            amazonDynamoDB.deleteTable(tableName);
            return ResponseEntity.ok("Deleted successfully");
        }
        catch (Exception exception)  {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> deleteItem(String tableName, String itemKey) {
        try {
            amazonDynamoDB.deleteItem(new DeleteItemRequest(tableName, getItemFromDb(tableName,itemKey)));
            return ResponseEntity.ok("Deleted successfully");
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cannot delete Item");
        }
    }

    private Map<String, AttributeValue> getItemFromDb(String tableName, String key) {
        HashMap<String, AttributeValue> key_to_get =
                new HashMap<String, AttributeValue>();

        key_to_get.put(tableName, new AttributeValue(key));

        GetItemRequest request = new GetItemRequest()
                .withKey(key_to_get)
                .withTableName(tableName);
        return amazonDynamoDB.getItem(request).getItem();
    }
}
