/**
 * 
 */
package com.duowan.ebguide.vo;


/**
 * Title:程序的中文名称
 * Description: 程序功能的描述 
 * 2013-4-25 
 */
public class JsonResponse {

    public static enum CODE {
        OK, FAIL
    }

    public static abstract class BaseJsonResponse {

        private CODE code;

        protected BaseJsonResponse(CODE code) {
            this.code = code;
        }

        public CODE getCode() {
            return code;
        }

    }

    public static class SuccessJsonResponse extends BaseJsonResponse {

        private Object data;

        public SuccessJsonResponse(Object data) {
            super(CODE.OK);
            this.data = data;

        }
        public SuccessJsonResponse()
        {
            super(CODE.OK);
        }
        public Object getData() {
            return data;
        }

    }

    public static class FailJsonResponse extends BaseJsonResponse {

        private String data;

        public FailJsonResponse(String data) {
            super(CODE.FAIL);
            this.data = data;
        }

        public String getData() {
            return data;
        }

    }
}
