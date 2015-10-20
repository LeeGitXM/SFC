/**
 *   (c) 2015  ILS Automation. All rights reserved.
 *  
 */
package com.ils.sfc.browser.gateway;

import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

/**
 *  This handler provides is a common class for handling requests
 *  from the designer or client (there is only one).
 */
public class GatewayRequestHandler {
	private final static String TAG = "GatewayRequestHandler";
	private final LoggerEx log;
	private final GatewayContext context;
	private final ClientReqSession session;
	private final Long projectId;

	/**
	 * Constructor.
	 */
	public GatewayRequestHandler(GatewayContext ctx,ClientReqSession sess,Long proj) {
		this.context = ctx;
		this.session = sess;
		this.projectId = proj;
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}
	
}

