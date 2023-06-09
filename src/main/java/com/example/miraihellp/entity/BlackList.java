package com.example.miraihellp.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

/**
 * @Author indinner
 * @Date 2023/6/9 16:37
 * @Version 1.0
 * @Doc:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "黑名单配置")
public class BlackList {

    @Id
    private String ID;

    private Long QQ;

}
