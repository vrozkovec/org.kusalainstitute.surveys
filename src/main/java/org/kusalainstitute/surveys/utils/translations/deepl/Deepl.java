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
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @author vit
 */
@Singleton
public class Deepl implements IDeepl
{
	@Inject
	private IDeeplApi deeplApi;

	private AtomicLong deeplConsumedCharacters = new AtomicLong(0);
	private AtomicLong deeplRemainingCharacters = new AtomicLong(0);

	/**
	 * Construct.
	 */
	public Deepl()
	{
	}

	/**
	 * @see org.kusalainstitute.surveys.utils.translations.deepl.IDeepl#translate(java.lang.String,
	 *      org.kusalainstitute.surveys.utils.translations.deepl.DeeplSourceLanguage,
	 *      org.kusalainstitute.surveys.utils.translations.deepl.DeeplTargetLanguage, boolean, boolean)
	 */
	@Override
	public String translate(String text, DeeplSourceLanguage sourceLanguage, DeeplTargetLanguage targetLanguage,
		boolean splitSentences, boolean preserverFormatting)
	{
		Call<TranslationResponse> call = deeplApi.tagSentence(text, sourceLanguage, targetLanguage, DeeplBoolean.of(splitSentences),
			DeeplBoolean.of(preserverFormatting));

		Response<TranslationResponse> response;
		try
		{
			response = call.execute();
		}
		catch (IOException e)
		{
			throw new RuntimeException("DeepL API call failed: " + e.getMessage(), e);
		}

		if (!response.isSuccessful() || response.body() == null)
		{
			String errorMsg = "DeepL API error: HTTP " + response.code();
			try
			{
				if (response.errorBody() != null)
				{
					errorMsg += " - " + response.errorBody().string();
				}
			}
			catch (IOException ignored)
			{
			}
			throw new RuntimeException(errorMsg);
		}

		List<Translation> translations = response.body().getTranslations();
		if (translations == null || translations.isEmpty())
		{
			return null;
		}

		return translations.get(0).getText();
	}

	/**
	 * @see org.kusalainstitute.surveys.utils.translations.deepl.IDeepl#updateUsage()
	 */
	@Override
	public void updateUsage()
	{
		try
		{
			Call<Usage> usage = deeplApi.getUsage();
			Response<Usage> response = usage.execute();
			Usage usageResponse = response.body();
			deeplConsumedCharacters.set(usageResponse.getCharacterCount());
			deeplRemainingCharacters.set(usageResponse.getCharacterLimit());
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see org.kusalainstitute.surveys.utils.translations.deepl.IDeepl#getUsage()
	 */
	@Override
	public Usage getUsage()
	{
		return new Usage(deeplConsumedCharacters.get(), deeplRemainingCharacters.get());
	}

}
