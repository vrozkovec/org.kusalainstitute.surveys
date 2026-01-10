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

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.annotations.SerializedName;
import com.google.inject.ImplementedBy;

/**
 * @author vit
 */
@ImplementedBy(Deepl.class)
public interface IDeepl extends Serializable
{
	/**
	 * How many characters is there left?
	 *
	 * @return remaining characters
	 */
	Usage getUsage();

	/**
	 * Updates usage.
	 */
	void updateUsage();

	/**
	 * Translates text using Deepl translator.
	 *
	 * @param text
	 * @param sourceLanguage
	 * @param targetLanguage
	 * @param splitSentences
	 * @param preserverFormatting
	 * @return translated text
	 */
	default String translate(String text, DeeplSourceLanguage sourceLanguage, DeeplTargetLanguage targetLanguage)
	{
		return translate(text, sourceLanguage, targetLanguage, false, true);
	}

	/**
	 * Translates text using Deepl translator.
	 *
	 * @param text
	 * @param sourceLanguage
	 * @param targetLanguage
	 * @param splitSentences
	 * @param preserverFormatting
	 * @return translated text
	 */
	String translate(String text, DeeplSourceLanguage sourceLanguage, DeeplTargetLanguage targetLanguage,
		boolean splitSentences, boolean preserverFormatting);


	/**
	 * @author vit
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TranslationResponse
	{
		private List<Translation> translations;

		/**
		 * @return translations
		 */
		public List<Translation> getTranslations()
		{
			return translations;
		}

	}

	/**
	 * Translation
	 *
	 * @author vit
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Translation
	{
		@SerializedName("detected_source_language")
		private String detectedSourceLanguage;

		private String text;

		/**
		 * @return detected source language
		 */
		public String getDetectedSourceLanguage()
		{
			return detectedSourceLanguage;
		}

		/**
		 * @return text
		 */
		public String getText()
		{
			return text;
		}

	}
	/**
	 * Usage
	 *
	 * @author vit
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Usage
	{
		@SerializedName("character_count")
		private long characterCount;

		@SerializedName("character_limit")
		private long characterLimit;

		/**
		 * Construct.
		 *
		 * @param characterCount
		 * @param characterLimit
		 */
		public Usage(long characterCount, long characterLimit)
		{
			super();
			this.characterCount = characterCount;
			this.characterLimit = characterLimit;
		}

		/**
		 * @return character count
		 */
		public long getCharacterCount()
		{
			return characterCount;
		}

		/**
		 * @return character limit
		 */
		public long getCharacterLimit()
		{
			return characterLimit;
		}

		/**
		 * @return characters remaining
		 */
		public long getCharactersRemaining()
		{
			return characterLimit - characterCount;
		}

		@Override
		public String toString()
		{
			return "Usage [characterCount=" + characterCount + ", characterLimit=" + characterLimit + "]";
		}

	}
	/**
	 * @author vit
	 */
	public static class DeeplLanguages
	{
		private List<DeeplLanguage> languages;

		/**
		 * Construct.
		 *
		 * @param languages
		 */
		@JsonCreator
		public DeeplLanguages(List<DeeplLanguage> languages)
		{
			super();
			this.languages = languages;
		}

		/**
		 * @return languages
		 */
		@JsonValue
		public List<DeeplLanguage> getLanguages()
		{
			return languages;
		}

	}

	/**
	 * @author vit
	 */
	public static class DeeplLanguage
	{
		@JsonProperty
		private String language;

		@JsonProperty
		private String name;

		/**
		 * @return language
		 */
		public String getLanguage()
		{
			return language;
		}

		/**
		 * @return name
		 */
		public String getName()
		{
			return name;
		}

		@Override
		public String toString()
		{
			return "Language [language=" + language + ", name=" + name + "]";
		}

	}
}
