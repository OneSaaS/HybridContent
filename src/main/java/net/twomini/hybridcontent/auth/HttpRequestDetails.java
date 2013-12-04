package net.twomini.hybridcontent.auth;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestDetails {

    public Map<String,String> cookies = new HashMap<String, String>();
    public Map<String,String> headers = new HashMap<String, String>();
    public String uri;

}
