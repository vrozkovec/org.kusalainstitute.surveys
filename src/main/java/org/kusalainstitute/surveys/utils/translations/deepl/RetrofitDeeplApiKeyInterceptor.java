/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kusalainstitute.surveys.utils.translations.deepl;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author vit
 */
public class RetrofitDeeplApiKeyInterceptor implements Interceptor
{

	private String apiKey;

	/**
	 * Construct.
	 *
	 * @param apiKey
	 */
	public RetrofitDeeplApiKeyInterceptor(String apiKey)
	{
		this.apiKey = apiKey;
	}

	@Override
	public Response intercept(Chain chain) throws IOException
	{
		var request = chain.request().newBuilder();
		var originalHttpUrl = chain.request().url();
		var newUrl = originalHttpUrl.newBuilder().addQueryParameter("auth_key", apiKey).build();
		request.url(newUrl);
		return chain.proceed(request.build());
	}
}