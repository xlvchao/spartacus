package com.xlc.spartacus.article.aspect;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ArticleScan {
    /**
     * 描述
     */
    String description()  default "";
}
