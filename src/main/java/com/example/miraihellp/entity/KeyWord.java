package com.example.miraihellp.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

/**
 * @Author indinner
 * @Date 2023/6/8 18:10
 * @Version 1.0
 * @Doc:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "关键词配置")
public class KeyWord {

    @Id
    private String ID;

    private String content;

    @ApiModelProperty(value = "关键词等级,0撤回,1禁言,2移除,3移除且拉黑")
    private Integer state;


}
