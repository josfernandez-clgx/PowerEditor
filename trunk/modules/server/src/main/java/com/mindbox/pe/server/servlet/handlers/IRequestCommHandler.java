package com.mindbox.pe.server.servlet.handlers;

import com.mindbox.pe.communication.RequestComm;
import com.mindbox.pe.communication.ResponseComm;
import javax.servlet.http.HttpServletRequest;

/**
 * T presents the request comm that this expects.
 * @author kim
 *
 */
public interface IRequestCommHandler<T extends RequestComm<?>> {

	ResponseComm serviceRequest(T requestcomm, HttpServletRequest httpservletrequest);
}