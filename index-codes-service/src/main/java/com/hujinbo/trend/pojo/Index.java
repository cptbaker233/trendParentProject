package com.hujinbo.trend.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Index implements Serializable {
    private String code;
    private String name;
}
