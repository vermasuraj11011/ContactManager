package com.contactManager.helper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {

    private String content;
    private String type;
}
