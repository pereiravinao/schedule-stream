package com.br.authenticator.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Document("refresh_tokens")
public class RefreshTokenEntity {
    @Id
    private String id;

    @Field("token")
    private String token;

    @Field("user_id")
    private String userId;

}
