package com.obviz.review.models;

import java.io.Serializable;

/**
 * Created by gaylor on 24.07.15.
 *
 */
public class ID implements Serializable {

    private static final long serialVersionUID = 1857679936191403937L;

    private String $oid;

    public String getValue() {
        return $oid;
    }
}
