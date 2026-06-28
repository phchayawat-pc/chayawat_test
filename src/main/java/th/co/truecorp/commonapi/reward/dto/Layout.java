package th.co.truecorp.commonapi.reward.dto;

import lombok.Data;

import java.io.Serializable;


@Data
//@Document(collection = "testData")
public class Layout implements Serializable {

    private Integer id;
    private String layout_id;
    private String layout_name;
    private String brand_code;
    private String product_type_code;
    private String charge_type_code;
    private String start_date;
    private String end_date;
    private String valid_flag;
    private String layout_status;
    private String created_by;
    private String created_date;
    private String modified_by;
    private String modified_date;
    private Integer priority;


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("[Layout |");
        sb.append(" id=").append(id);
        sb.append(" layout_id=").append(layout_id);
        sb.append(" layout_name=").append(layout_name);
        sb.append(" brand_code=").append(brand_code);
        sb.append(" product_type_code=").append(product_type_code);
        sb.append(" charge_type_code=").append(charge_type_code);
        sb.append(" start_date=").append(start_date);
        sb.append(" end_date=").append(end_date);
        sb.append(" valid_flag=").append(valid_flag);
        sb.append(" layout_status=").append(layout_status);
        sb.append(" created_by=").append(created_by);
        sb.append(" created_date=").append(created_date);
        sb.append(" modified_by=").append(modified_by);
        sb.append(" modified_date=").append(modified_date);
        sb.append(" priority=").append(priority);
        sb.append("]");
        return sb.toString();
    }
}
