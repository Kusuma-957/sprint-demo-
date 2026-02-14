package com.hotelManagement.system.exception;


import lombok.Getter;

@Getter
public enum ApiCode {
    // Success codes (used in success responses)
    POSTSUCCESS, UPDATESUCCESS, DELETESUCCESS,

    // Failure codes from your CSV
    ADDFAILS, GETFAILS, GETALLFAILS, UPDTFAILS, DLTFAILS,

    // Generic/system/validation
    VALIDATION_FAILS, BADREQUEST, UNAUTHORIZED, FORBIDDEN, INTERNALERROR
}
