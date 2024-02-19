package com.example.miraihellp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @Author indinner
 * @Date 2024/2/19 11:18
 * @Version 1.0
 * @Doc:
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Points {

    @Id
    private String qq;

    private Integer points;

    private Long createTime=new Date().getTime();

}
