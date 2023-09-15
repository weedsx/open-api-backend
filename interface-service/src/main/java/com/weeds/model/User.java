package com.weeds.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weeds
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = -1203846046395257289L;
    private String name;
}
