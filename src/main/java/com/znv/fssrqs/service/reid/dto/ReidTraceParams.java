package com.znv.fssrqs.service.reid.dto;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午2:18
 */
public class ReidTraceParams {
    /**
     * query : {"bool":{"filter":{"term":{"fused_id":"338b37d6-3210-48f7-a25e-a82afc7358b9"}}}}
     */

    private QueryBean query;

    public QueryBean getQuery() {
        return query;
    }

    public void setQuery(QueryBean query) {
        this.query = query;
    }

    public static class QueryBean {
        /**
         * bool : {"filter":{"term":{"fused_id":"338b37d6-3210-48f7-a25e-a82afc7358b9"}}}
         */

        private BoolBean bool;

        public BoolBean getBool() {
            return bool;
        }

        public void setBool(BoolBean bool) {
            this.bool = bool;
        }

        public static class BoolBean {
            /**
             * filter : {"term":{"fused_id":"338b37d6-3210-48f7-a25e-a82afc7358b9"}}
             */

            private FilterBean filter;

            public FilterBean getFilter() {
                return filter;
            }

            public void setFilter(FilterBean filter) {
                this.filter = filter;
            }

            public static class FilterBean {
                /**
                 * term : {"fused_id":"338b37d6-3210-48f7-a25e-a82afc7358b9"}
                 */

                private TermBean term;

                public TermBean getTerm() {
                    return term;
                }

                public void setTerm(TermBean term) {
                    this.term = term;
                }

                public static class TermBean {
                    /**
                     * fused_id : 338b37d6-3210-48f7-a25e-a82afc7358b9
                     */

                    private String fused_id;

                    public String getFused_id() {
                        return fused_id;
                    }

                    public void setFused_id(String fused_id) {
                        this.fused_id = fused_id;
                    }
                }
            }
        }
    }
}
