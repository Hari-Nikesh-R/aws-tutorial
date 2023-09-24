package com.example.entity;

import com.example.entity.helper.Encrypt;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "one_time_password")
@Data
public class OneTimePassword {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Convert(converter = Encrypt.class)
    private String email;

    @Convert(converter = Encrypt.class)
    private String password;
}
