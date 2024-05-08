/*
 * Copyright 2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.hooks.model.request;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

public class FhirAuthorization {
	private static final String BEARER = "Bearer";

	@SerializedName("access_token")
	private String accessToken;
	@SerializedName("token_type")
	private String tokenType;
	@SerializedName("expires_in")
	private int expiresIn;
	private String scope;
	private String subject;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public String toString() {
		return "FhirAuthorization [access_token=" + accessToken + ", token_type=" + tokenType + ", expiresIn="
				+ expiresIn + ", scope=" + scope + ", subject=" + subject + "]";
	}

	public boolean allFieldsValid() {
		return StringUtils.isNoneBlank(accessToken, tokenType, scope, subject)
				&& StringUtils.compareIgnoreCase(tokenType, BEARER) == 0
				&& expiresIn > 0;
	}
}
