/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.chile.core.annotations;

import java.lang.annotation.*;

/**
 * Defines a DDL code to be generated by tooling.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(DdlByDbms.class)
public @interface Ddl {

    /**
     * DBMS type for which to apply this DDL code. If not specified, this DDL is the default for any DBMS.
     */
    String dbms() default "";

    /**
     * DDL code.
     */
    String value();
}
