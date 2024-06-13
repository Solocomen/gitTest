/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.db.TblColumn
 * @author	TedKwan
 * @date	29-Sep-2022
 * Description:
 *
 * History:
 * 20220929-TedKwan: Created
 */
package epc.epcsalesapi.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TblColumn {
	public String name() default "";
}
