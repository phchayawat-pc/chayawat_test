package th.co.truecorp.commonapi.reward.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PagedResult<T> {
    private List<T> items;
    private int totalCount;
    private int totalPages;

    public PagedResult(List<T> items, int totalCount, int totalPages) {
        this.items = items;
        this.totalCount = totalCount;
        this.totalPages = totalPages;
    }
}
