package org.base.rest;

import org.apache.juneau.rest.RestGuard;
import org.apache.juneau.rest.RestRequest;

public class ShiroGuard extends RestGuard {

	@Override
	public boolean isRequestAllowed(RestRequest req) {
		// TODO Auto-generated method stub
		return true;
	}
}
