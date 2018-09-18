package com.mindbox.pe.communication.pear;

import com.mindbox.pe.communication.pear.Request;
import com.mindbox.pe.communication.pear.Response;

public interface Transporter {

    Response sendRequest(Request<?> request) throws Exception;
}
