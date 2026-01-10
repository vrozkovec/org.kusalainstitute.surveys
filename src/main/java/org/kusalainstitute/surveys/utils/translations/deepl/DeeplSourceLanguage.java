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

/**
 * @author vit
 */
public enum DeeplSourceLanguage
{
	/** Arabic */
	ARABIC("AR"),
	/** Bulgarian */
	BULGARIAN("BG"),
	/** Czech */
	CZECH("CS"),
	/** Danish */
	DANISH("DA"),
	/** German */
	GERMAN("DE"),
	/** Greek */
	GREEK("EL"),
	/** English (all English variants) */
	ENGLISH("EN"),
	/** Spanish */
	SPANISH("ES"),
	/** Estonian */
	ESTONIAN("ET"),
	/** Finnish */
	FINNISH("FI"),
	/** French */
	FRENCH("FR"),
	/** Hungarian */
	HUNGARIAN("HU"),
	/** Indonesian */
	INDONESIAN("ID"),
	/** Italian */
	ITALIAN("IT"),
	/** Japanese */
	JAPANESE("JA"),
	/** Korean */
	KOREAN("KO"),
	/** Lithuanian */
	LITHUANIAN("LT"),
	/** Latvian */
	LATVIAN("LV"),
	/** Norwegian Bokmål */
	NORWEGIAN_BOKMAL("NB"),
	/** Dutch */
	DUTCH("NL"),
	/** Polish */
	POLISH("PL"),
	/** Portuguese (all Portuguese variants) */
	PORTUGUESE("PT"),
	/** Romanian */
	ROMANIAN("RO"),
	/** Russian */
	RUSSIAN("RU"),
	/** Slovak */
	SLOVAK("SK"),
	/** Slovenian */
	SLOVENIAN("SL"),
	/** Swedish */
	SWEDISH("SV"),
	/** Turkish */
	TURKISH("TR"),
	/** Ukrainian */
	UKRAINIAN("UK"),
	/** Chinese (all Chinese variants) */
	CHINESE("ZH"),

	/** */
	;

	private String languageString;

	/**
	 * Construct.
	 *
	 * @param languageString
	 */
	private DeeplSourceLanguage(String languageString)
	{
		this.languageString = languageString;
	}

	@Override
	public String toString()
	{
		return languageString;
	}

}
