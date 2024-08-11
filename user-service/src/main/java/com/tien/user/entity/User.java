package com.tien.user.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document("user")
public class User {

    @MongoId
    String profileId;

    String userId;
    String email;
    String username;
    String firstName;
    String lastName;
    LocalDate dob;

}