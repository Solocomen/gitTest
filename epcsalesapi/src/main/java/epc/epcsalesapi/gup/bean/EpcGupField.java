package epc.epcsalesapi.gup.bean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EpcGupField {
    String fieldName();
    String fieldType();
}