package com.yhl.orm.componet.constant;

import lombok.Data;
import org.springframework.util.ObjectUtils;

@Data
public class UpdateCondition {
    private UpdateFields updateFields;
    private  WhereCondition whereCondition;
    public UpdateFields updateFields(){
        if (ObjectUtils.isEmpty(updateFields)){
            updateFields =new UpdateFields();
        }
        return updateFields;
    }
    public WhereCondition whereCondition(){
        if (ObjectUtils.isEmpty(whereCondition)){
            whereCondition =new WhereCondition();
        }
        return whereCondition;
    }

}
