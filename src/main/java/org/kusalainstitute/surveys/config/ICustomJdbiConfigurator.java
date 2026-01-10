/**
 *
 */
package org.kusalainstitute.surveys.config;

import java.io.Serializable;

import org.jdbi.v3.core.Jdbi;

import com.zaxxer.hikari.HikariConfig;

/**
 * @author vit
 *
 */
public interface ICustomJdbiConfigurator extends Serializable
{
	/**
	 * Called when Jdbi instance is initialized.
	 *
	 * @param jdbi
	 */
	void onInitialize(Jdbi jdbi);

	/**
	 * Allows each project to configure pool on their own.
	 *
	 * @param hc
	 */
	default void configurePool(HikariConfig hc)
	{

	}
}
