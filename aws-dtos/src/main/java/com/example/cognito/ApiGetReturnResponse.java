/*
 * Copyright (C) 2023-2024 Kaytes Pvt Ltd. The right to copy, distribute, modify, or otherwise
 * make use of this software may be licensed only pursuant to the terms of an applicable Kaytes Pvt Ltd license agreement.
 */
package com.example.cognito;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The ApiGetReturnResponse class is used for the common response to be sent for GET operations
 */
@Getter
@Setter
public class ApiGetReturnResponse extends ApiReturnResponse{

	private static final long serialVersionUID = 1L;
	
	private transient List<Object> retrievedResult;
	
}
