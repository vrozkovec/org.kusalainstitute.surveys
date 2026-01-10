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

import java.util.concurrent.TimeUnit;

import com.google.inject.Provider;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @author vit
 */
public class RetrofitDeeplProvider implements Provider<IDeeplApi>
{
	private String baseUrl;
	private String apiKey;

	/**
	 * Construct.
	 *
	 * @param baseUrl
	 * @param apiKey
	 */
	public RetrofitDeeplProvider(String baseUrl, String apiKey)
	{
		this.baseUrl = baseUrl;
		this.apiKey = apiKey;
	}

	@Override
	public IDeeplApi get()
	{
		OkHttpClient client = new OkHttpClient.Builder().connectTimeout(40, TimeUnit.SECONDS)
			.readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
			.addInterceptor(new RetrofitDeeplApiKeyInterceptor(apiKey)).build();

		Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(ScalarsConverterFactory.create())
			.addConverterFactory(GsonConverterFactory.create()).client(client).build();

		return retrofit.create(IDeeplApi.class);
	}
}

