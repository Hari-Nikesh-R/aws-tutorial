package com.example.dynamodb;

import lombok.Data;

@Data
public class MusicRequest {
    private String artist;
    private String songTitle;
    private String albumTitle;
    private int awards;

}
