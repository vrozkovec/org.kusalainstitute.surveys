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

import org.kusalainstitute.surveys.utils.translations.deepl.IDeepl.TranslationResponse;
import org.kusalainstitute.surveys.utils.translations.deepl.IDeepl.Usage;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author vit
 */
interface IDeeplApi
{
	/**
	 * @param text
	 * @param sourceLanguage
	 * @param targetLanguage
	 * @param splitSentences
	 * @param preserveFormatting
	 * @return translation
	 */
	@GET("translate")
	Call<TranslationResponse> translateSentence(@Query("text") String text, @Query("source_lang") DeeplSourceLanguage sourceLanguage,
		@Query("target_lang") DeeplTargetLanguage targetLanguage, @Query("split_sentences") DeeplBoolean splitSentences,
		@Query("preserve_formatting") DeeplBoolean preserveFormatting);

	/**
	 * How many characters is there left from the free quota?
	 *
	 * @return usage
	 */
	@GET("usage")
	Call<Usage> getUsage();


}
