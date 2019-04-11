package com.yhl.securityCommon.access;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.ConfigAttribute;

@Getter
@Setter
public class RequestAuthorityAttribute implements ConfigAttribute {

    private static final long serialVersionUID = 5861389645003319286L;

    private String apiUri;

    private HttpMethod method;

    private String mactherType;

    private boolean accessVisit =false;

    public RequestAuthorityAttribute() {
        super();
    }

    public RequestAuthorityAttribute(String apiUri, HttpMethod method, String mactherType) {
        super();
        this.apiUri = apiUri;
        this.method = method;
        this.mactherType  = mactherType;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((apiUri == null) ? 0 : apiUri.hashCode());
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        result = prime * result + ((mactherType == null) ? 0 : mactherType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RequestAuthorityAttribute other = (RequestAuthorityAttribute) obj;
        if (apiUri == null) {
            if (other.apiUri != null)
                return false;
        } else if (!apiUri.equals(other.apiUri))
            return false;
        if (method != other.method)
            return false;
        return true;
    }

    @Override
    public String getAttribute() {
        return toString();
    }
}
