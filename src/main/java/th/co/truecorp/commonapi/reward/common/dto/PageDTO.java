package th.co.truecorp.commonapi.reward.common.dto;

import lombok.Data;

@Data
public class PageDTO {
    private Integer pageNumber;
    private Integer pageSize;
    private Integer count;
    private Integer totalPage;

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("[PageDTO |");
        sb.append(" pageNumber=").append(getPageNumber());
        sb.append(" pageSize=").append(getPageSize());
        sb.append(" count=").append(getCount());
        sb.append(" totalPage=").append(getTotalPage());
        sb.append("]");
        return sb.toString();
    }
}
