package com.teamresourceful.resourcefulbees.common.lib.tools;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TranslatableComponents should be used ONLY for static contexts. They should never be used if <code>.append</code> is happening to them.
 * Use a string of the translation key if appending is needed and use string formats where possible instead of <code>.append</code>
 * <p>
 * If you add classes to lib.constants.translations remember to add the class name to the ModLanguageProvider super!
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Translate {

    String value();
}
