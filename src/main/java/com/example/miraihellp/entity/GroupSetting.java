package com.example.miraihellp.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

/**
 * @Author indinner
 * @Date 2023/6/8 15:43
 * @Version 1.0
 * @Doc:群设置
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "群聊配置")
public class GroupSetting {

    @Id
    private Long ID;

    @ApiModelProperty(value = "是否开启第二课堂通知,默认为false")
    private Boolean twoClass=false;

    @ApiModelProperty(value = "是否开启关键词撤回")
    private Boolean keyword=false;

    @ApiModelProperty(value = "是否开启加群验证")
    private Boolean joinKey=false;

    @ApiModelProperty(value = "加群验证的答案")
    private String joinAnswer;


}
