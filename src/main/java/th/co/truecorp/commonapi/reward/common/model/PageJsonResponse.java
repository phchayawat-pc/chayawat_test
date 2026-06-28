package th.co.truecorp.commonapi.reward.common.model;

import th.co.truecorp.commonapi.reward.common.dto.PageDTO;
import th.co.truecorp.commonlib.log.model.TransactionResult;
import th.co.truecorp.commonlib.ws.model.StatusJsonResponse;

import java.util.HashMap;

public class PageJsonResponse<TBody> {
        private StatusJsonResponse status;
        private TBody data;
        private PageDTO paging;
        private Object debug;

        public PageJsonResponse(HashMap<String, Object> tv, TransactionResult transactionResult, TBody data, PageDTO page1, Object debug) {
            StatusJsonResponse statusJsonResponse = new StatusJsonResponse(transactionResult);
            statusJsonResponse.merge(tv);
            this.status = statusJsonResponse;
            this.data = data;
            this.paging = page1;
            this.debug = debug;
        }

        public StatusJsonResponse getStatus() {
            return this.status;
        }

        public TBody getData() {
            return this.data;
        }
        public PageDTO getPage() {
            return  this.paging;
        }
        public Object getDebug() {
            return debug;
        }

    public void setStatus(final StatusJsonResponse status) {
            this.status = status;
        }

        public void setData(final TBody data) {
            this.data = data;
        }
        public void setPage(final PageDTO page) {
        this.paging = page;
    }
        public void setDebug(Object debug) {
            this.debug = debug;
        }

    public boolean equals(final Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof PageJsonResponse)) {
                return false;
            } else {
                PageJsonResponse<?> other = (PageJsonResponse)o;
                if (!other.canEqual(this)) {
                    return false;
                } else {
                    Object this$status = this.getStatus();
                    Object other$status = other.getStatus();
                    if (this$status == null) {
                        if (other$status != null) {
                            return false;
                        }
                    } else if (!this$status.equals(other$status)) {
                        return false;
                    }

                    Object this$data = this.getData();
                    Object other$data = other.getData();
                    if (this$data == null) {
                        if (other$data != null) {
                            return false;
                        }
                    } else if (!this$data.equals(other$data)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        protected boolean canEqual(final Object other) {
            return other instanceof PageJsonResponse;
        }

        public String toString() {
            StatusJsonResponse var10000 = this.getStatus();
            return "PageJsonResponse(status=" + var10000 + ", data=" + this.getData() + ")";
        }

        public PageJsonResponse() {
        }

        public PageJsonResponse(final StatusJsonResponse status, final TBody data, final PageDTO page, Object debug) {
            this.status = status;
            this.data = data;
            this.paging = page;
            this.debug = debug;
        }
    }


